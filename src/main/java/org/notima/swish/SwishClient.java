package org.notima.swish;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SwishClient {	
	private final String BASE_URL_PRODUCTION = "https://cpc.getswish.net/swish-cpcapi";
	private final String BASE_URL_TEST = "https://mss.cpc.getswish.net/swish-cpcapi";
	private final String URL_PAYMENTREQUESTS = "/api/v2/paymentrequests/";
	
	private boolean test = false;
	private KeyStore keyStore;
	private String keyStorePassword;
	private KeyStore trustStore;
	private KeyManagerFactory keyStoreManagerFactory;
	private TrustManagerFactory trustStoreManagerFactory;
	private Gson gson;

	public static String generateInstructionUUID() {
		Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < 32){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, 32).toUpperCase();
	}
	
	public SwishClient(KeyStore keyStore, String keyStorePassword, KeyStore trustStore) throws SwishException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		this(false, keyStore, keyStorePassword, trustStore);
	}
	
	/**
	 * @param test Whether or not the test url should be used.
	 * @throws SwishException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws FileNotFoundException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 */
	public SwishClient(boolean test, KeyStore keyStore, String keyStorePassword, KeyStore trustStore) throws SwishException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		this.test = test;
		this.keyStore = keyStore;
		this.keyStorePassword = keyStorePassword;
		this.trustStore = trustStore;
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		initKeyStores();
	}

	private void initKeyStores() throws UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		/*keyStore = KeyStore.getInstance("pkcs12");
		keyStore.load(new FileInputStream(certPath), certPass.toCharArray());*/
		keyStoreManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyStoreManagerFactory.init(keyStore, keyStorePassword.toCharArray());
		
		/*FileInputStream str = new FileInputStream(cacertPath);
		trustStore = KeyStore.getInstance("JKS");
		trustStore.load(str, cacertPass.toCharArray());*/
		trustStoreManagerFactory = TrustManagerFactory
				.getInstance("SunX509");
		trustStoreManagerFactory.init(trustStore);
	}
	
	/**
	 * Send a payment request in order to receive a payment.
	 * @param request Payment request.
	 * @return url, used to check payment status.
	 * @throws SwishException
	 */
	public SwishResponseHeaders sendPaymentRequest(PaymentRequest request, String instructionUUID) throws SwishException {
		SSLSocketFactory defaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
		SwishResponseHeaders responseHeaders = null;
		URL url;
		try {
			url = new URL(null, getBaseUrl() + URL_PAYMENTREQUESTS + instructionUUID, new sun.net.www.protocol.https.Handler());
			String paymentRequestJson = gson.toJson(request);
			HttpsURLConnection conn = openConnection(url, "PUT");
			addBodyToConnection(conn, paymentRequestJson);
			int responseCode = conn.getResponseCode();
			if (responseCode ==  HttpsURLConnection.HTTP_CREATED) {
			    responseHeaders = getResponseHeaders(conn);		
			} else {
				HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);
				conn.disconnect();	
				throw new SwishException(String.format("Response from %s:\n%s", url.toString(), getErrorBodyFromConnection(conn)));
			}
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);
			conn.disconnect();
			return responseHeaders;
		} catch (Exception e) {
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);
			throw new SwishException("Payment request failed", e);
		}
	}
	
	private SwishResponseHeaders getResponseHeaders(HttpsURLConnection conn) {
		Map<String, List<String>> hdrs = conn.getHeaderFields();
		String location = hdrs.get("Location").get(0);
		String requestToken = null;
		if(hdrs.get("PaymentRequestToken") != null)
			requestToken = hdrs.get("PaymentRequestToken").get(0);
		return new SwishResponseHeaders(location, requestToken);
	}
	/**
	 * Get Swish api URL
	 * @return either test or production URL depending on if this object is in test mode or not.
	 */
	private String getBaseUrl() {
		String url = test ? BASE_URL_TEST : BASE_URL_PRODUCTION;
		return url;
	}
	
	/**
	 * @param url url, used to check payment status
	 * @return Payment response containing status an payment information.
	 */
	public SwishPayment retrievePayment(String url) throws SwishException, MalformedURLException {
		return retrievePayment(new URL(url));
	}
	
	public SwishPayment retrievePayment(URL url) throws SwishException {
		SSLSocketFactory defaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
		try {
			HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
			HttpsURLConnection conn = openConnection(url, "GET");
			String body = getBodyFromConnection(conn);
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);	
			return gson.fromJson(body, SwishPayment.class);
		} catch (Exception e) {
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);
			throw new SwishException("", e);
		}
	}
	
	private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslctx = SSLContext.getInstance("TLSv1.2");
		sslctx.init(keyStoreManagerFactory.getKeyManagers(), 
				trustStoreManagerFactory.getTrustManagers(), null);
		return sslctx;
	}
	
	private HttpsURLConnection openConnection(URL url, String method) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(true);			
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setUseCaches(false);
		return conn;
	}
	
	private void addBodyToConnection(HttpsURLConnection conn, String body) throws IOException {
		OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
		wr.write(body);
		wr.flush();
		wr.close();
	}
	
	private String getBodyFromConnection(HttpsURLConnection conn) throws IOException {
		if(conn.getResponseCode() / 100 != 2)
			return null;
		InputStream stream = conn.getInputStream();
		InputStreamReader streamReader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(streamReader);
		StringBuilder response = new StringBuilder();
	    String currentLine;

	    while ((currentLine = reader.readLine()) != null) 
	        response.append(currentLine);

	    reader.close();
	    return response.toString();
	}

	private String getErrorBodyFromConnection(HttpsURLConnection conn) {
		InputStream stream = conn.getErrorStream();
		if(stream == null)
			return "";
		Scanner scanner = new Scanner(stream,"UTF-8");
		String errorString = scanner.useDelimiter("\\A").next();
		scanner.close();
		return errorString;
	}
}
