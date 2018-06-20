package swish;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SwishClient {
	/**
	 * Path of the CA certificate
	 */
	private String cacertPath;
	/**
	 * CA certificate password
	 */
	private String cacertPass;
	/**
	 * Path of the Swish certificate
	 */
	private String certPath;
	/**
	 * certificate password
	 */
	private String certPass;
	
	private final String URL_PRODUCTION = "https://swicpc.bankgirot.se/swish-cpcapi/api/v1/paymentrequests/";
	private final String URL_TEST = "https://mss.swicpc.bankgirot.se/swish-cpcapi/api/v1/paymentrequests/";
	
	private boolean test = false;
	private KeyStore keyStore;
	private KeyStore trustStore;
	private KeyManagerFactory keyStoreManagerFactory;
	private TrustManagerFactory trustStoreManagerFactory;
	private Gson gson;
	
	public SwishClient() throws SwishException {
		this(false);
	}
	
	/**
	 * @param test Whether or not the test url should be used.
	 * @throws SwishException
	 */
	public SwishClient(boolean test) throws SwishException {
		this.test = test;
		try {
			loadProperties();
			initKeyStores();
		} catch (Exception e) {
			throw new SwishException("", e);
		}
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	private void loadProperties() throws IOException {
		Properties prop = new Properties();
    	InputStream input = new FileInputStream("swish.properties");
    	prop.load(input);
    	cacertPath = prop.getProperty("cacertPath");
    	cacertPass = prop.getProperty("cacertPass");
    	certPath = prop.getProperty("certPath");
    	certPass = prop.getProperty("certPass");
	}

	private void initKeyStores() throws UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		keyStore = KeyStore.getInstance("pkcs12");
		keyStore.load(new FileInputStream(certPath), certPass.toCharArray());
		keyStoreManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyStoreManagerFactory.init(keyStore, certPass.toCharArray());
		
		FileInputStream str = new FileInputStream(cacertPath);
		trustStore = KeyStore.getInstance("JKS");
		trustStore.load(str, cacertPass.toCharArray());
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
	@SuppressWarnings("restriction")
	public SwishResponseHeaders sendPaymentRequest(PaymentRequest request) throws SwishException {
		SwishResponseHeaders responseHeaders = null;
		URL url;
		try {
			url = new URL(null, getUrl(), new sun.net.www.protocol.https.Handler());
			SSLSocketFactory defaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
			String paymentRequestJson = gson.toJson(request);
			HttpsURLConnection conn = openConnection(url, "POST");
			addBodyToConnection(conn, paymentRequestJson);
			int responseCode = conn.getResponseCode();
			if (responseCode ==  HttpsURLConnection.HTTP_CREATED) {
			    responseHeaders = getResponseHeaders(conn);		
			} else {
				InputStream stream = conn.getErrorStream();
				String errorString = new Scanner(stream,"UTF-8").useDelimiter("\\A").next();
				HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);	
				throw new SwishException(errorString);
			}
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);
			conn.disconnect();
			return responseHeaders;
		} catch (Exception e) {
			throw new SwishException("Payment request failed", e);
		}
	}
	
	SwishResponseHeaders getResponseHeaders(HttpsURLConnection conn) {
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
	String getUrl() {
		String url = test ? URL_TEST : URL_PRODUCTION;
		return url;
	}
	
	/**
	 * @param url url, used to check payment status
	 * @return Payment response containing status an payment information.
	 */
	public SwishPayment checkPaymentStatus(String url) throws SwishException, MalformedURLException {
		return checkPaymentStatus(new URL(url));
	}
	
	public SwishPayment checkPaymentStatus(URL url) throws SwishException {
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
	
	SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslctx = SSLContext.getInstance("TLSv1.2");
		sslctx.init(keyStoreManagerFactory.getKeyManagers(), 
				trustStoreManagerFactory.getTrustManagers(), null);
		return sslctx;
	}
	
	HttpsURLConnection openConnection(URL url, String method) throws IOException {
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(true);			
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setUseCaches(false);
		return conn;
	}
	
	void addBodyToConnection(HttpsURLConnection conn, String body) throws IOException {
		OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
		wr.write(body);
		wr.flush();
		wr.close();
	}
	
	String getBodyFromConnection(HttpsURLConnection conn) throws IOException {
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
}
