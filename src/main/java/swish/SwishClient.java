package swish;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SwishClient {
	private final String CACERT_PATH = "/home/parkado/certificates/Swish/truststore.jks";
	private final String CACERT_PASS = "test";
	private final String CERT_PATH = "/home/parkado/certificates/Swish/1231181189.p12";
	private final String CERT_PASS = "swish";
	
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
			initKeyStores();
		} catch (Exception e) {
			throw new SwishException("", e);
		}
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	private void initKeyStores() throws UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		keyStore = KeyStore.getInstance("pkcs12");
		keyStore.load(new FileInputStream(CERT_PATH), CERT_PASS.toCharArray());
		keyStoreManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyStoreManagerFactory.init(keyStore, CERT_PASS.toCharArray());
		
		FileInputStream str = new FileInputStream(CACERT_PATH);
		trustStore = KeyStore.getInstance("JKS");
		trustStore.load(str, CACERT_PASS.toCharArray());
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
	public String sendPaymentRequest(PaymentRequest request) throws SwishException {
		String response = "";
		URL url;
		try {
			url = new URL(null, getUrl(), new sun.net.www.protocol.https.Handler());
			SSLSocketFactory defaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
			String paymentRequestJson = gson.toJson(request);
			System.out.println(paymentRequestJson);
			HttpsURLConnection conn = openConnection(url, "POST");
			addBodyToConnection(conn, paymentRequestJson);
			int responseCode = conn.getResponseCode();
			System.out.println(responseCode);
			if (responseCode ==  HttpsURLConnection.HTTP_CREATED) { // HTTP_CREATED=201 means that swish has accepted the request
			    // The "location" parameter is found in headings
				Map<String, List<String>> hdrs = conn.getHeaderFields();
				response += hdrs.get("Location");	
				HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);			
			} else {
				InputStream stream = conn.getErrorStream();
				String errorString = new Scanner(stream,"UTF-8").useDelimiter("\\A").next();
				HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);	
				throw new SwishException(errorString);
			}
			conn.disconnect();
			return response.replaceAll("[\\[\\]]", "");
		} catch (Exception e) {
			throw new SwishException("", e);
		}
	}
	
	String getUrl() {
		String url = test ? URL_TEST : URL_PRODUCTION;
		return url;
	}
	
	/**
	 * @param url url, used to check payment status
	 * @return Payment response containing status an payment information.
	 */
	public PaymentResponse checkPaymentStatus(String url) throws SwishException, MalformedURLException {
		return checkPaymentStatus(new URL(url));
	}
	
	public PaymentResponse checkPaymentStatus(URL url) throws SwishException {
		SSLSocketFactory defaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
		try {
			HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
			HttpsURLConnection conn = openConnection(url, "GET");
			InputStream stream = conn.getInputStream();
			System.out.println(conn.getResponseCode());
		    Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name());
		    String out = scanner.useDelimiter("\\A").next();
		    scanner.close();
			System.out.println(out);
			HttpsURLConnection.setDefaultSSLSocketFactory(defaultSocketFactory);	
			return gson.fromJson(out, PaymentResponse.class);
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
}
