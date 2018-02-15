import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import swish.PaymentRequest;
import swish.SwishClient;
import swish.SwishException;

public class TestSwish {
	@Ignore
	@Test
	public void testSwish() throws Exception {
		String[] args = {
				"1",
				"https://mss.swicpc.bankgirot.se/swish-cpcapi/api/v1/paymentrequests/",
				"C:/SwishCert/Testcertifikat/PKCS12/truststore.jks",
				"test",
				"C:/SwishCert/Testcertifikat/PKCS12/1231181189.p12",
				"swish",
				"0123456789",
				"https://example.com/api/swishcb/paymentrequests",
				"46731930431",
				"1231181189",
				"100",
				"SEK",
				"TESTING",
				"123"
		};
		Swish.main(args);
	}
	
	@Test
	public void testPaymentRequest() throws SwishException, MalformedURLException {
		SwishClient client = new SwishClient(true);
		PaymentRequest request = new PaymentRequest();
		request.setCallbackUrl("https://services.notima.se/parkado-rest/ws/payment/swish-callback");
		request.setPayeeAlias("1231181189");
		request.setPayerAlias("46731930431");
		request.setAmount("100");
		String response = client.sendPaymentRequest(request);
		System.out.println(response);
		testCheckStatus(response);
	}
	
	public void testCheckStatus(String url) throws SwishException, MalformedURLException {
		SwishClient client = new SwishClient(true);
		client.checkPaymentStatus(new URL(url));
	}
}
