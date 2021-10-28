import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.notima.swish.PaymentRequest;
import org.notima.swish.SwishClient;
import org.notima.swish.SwishException;
import org.notima.swish.SwishPayment;
import org.notima.swish.SwishResponseHeaders;

public class TestSwish {
	private static final int E_COMMERCE = 0;
	private static final int M_COMMERCE = 1;

	private static final String CERTIFICATE_FILE = "Swish_Merchant_TestCertificate_1234679304.p12";
	private static final String CA_CERTIFICATE_FILE = "Swish_TLS_RootCA.jks";

	@Test
	public void testGetInstructionUUID(){
		System.out.println("\nTEST CREATE INSTRUCTION UUID");
		String uuid = SwishClient.generateInstructionUUID();
		System.out.println(uuid);
		assertEquals(32, uuid.length());
	}
	
	@Test
	public void testPaymentRequestECommerce() throws SwishException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		System.out.println("\nTESTING E-COMMERCE");
		SwishClient client = getTestClient();
		SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(E_COMMERCE), SwishClient.generateInstructionUUID());
		System.out.println(response.getLocation());
		testCheckStatus(client, response.getLocation(), SwishPayment.CREATED);
	}
	
	@Test
	public void testPaymentRequestMCommerce() throws SwishException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		System.out.println("\nTESTING M-COMMERCE");
		SwishClient client = getTestClient();
		SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(M_COMMERCE), SwishClient.generateInstructionUUID());
		System.out.println(response.getLocation());
		System.out.println(response.getPaymentRequestToken());
		testCheckStatus(client, response.getLocation(), SwishPayment.CREATED);
	}
	
	void testCheckStatus(SwishClient client, String url, String expectedStatus) throws SwishException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
		SwishPayment payment = client.retrievePayment(url);
		System.out.println("Status: " + payment.getStatus());
		assertEquals(expectedStatus, payment.getStatus());
	}

	SwishClient getTestClient() throws UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, SwishException, IOException {
		InputStream certInputStream = this.getClass().getClassLoader().getResourceAsStream(CERTIFICATE_FILE);
		KeyStore keyStore = KeyStore.getInstance("pkcs12");
		keyStore.load(certInputStream, "swish".toCharArray());

		InputStream str = this.getClass().getClassLoader().getResourceAsStream(CA_CERTIFICATE_FILE);
		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(str, "swish!".toCharArray());

		return new SwishClient(true, keyStore, "swish", trustStore);
	}
	
	PaymentRequest getTestRequest(int type) {
		return getTestRequest(type, null);
	}
	
	PaymentRequest getTestRequest(int type, String error) {
		PaymentRequest request = new PaymentRequest();
		request.setCallbackUrl("https://myfakehost.se/swishcallback.cfm");
		request.setPayeeAlias("1231181189");
		if(type == E_COMMERCE)
			request.setPayerAlias("46731930431");
		request.setAmount("100");
		if(error != null)
			request.setMessage(error);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println("Body:\n" + gson.toJson(request));

		return request;
	}
}
