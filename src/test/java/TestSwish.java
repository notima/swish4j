import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;
import swish.PaymentRequest;
import swish.SwishClient;
import swish.SwishException;
import swish.SwishPayment;
import swish.SwishResponseHeaders;

public class TestSwish {
	private static final int E_COMMERCE = 0;
	private static final int M_COMMERCE = 1;
	
	private static final String TRANSACTION_DECLINED_ERROR = "RF07";
	
	@Test
	public void testPaymentRequestECommerce() throws SwishException, MalformedURLException {
		System.out.println("\nTESTING E-COMMERCE");
		SwishClient client = new SwishClient(true);
		SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(E_COMMERCE));
		System.out.println(response.getLocation());
		testCheckStatus(response.getLocation());
	}
	
	@Test
	public void testPaymentRequestMCommerce() throws SwishException, MalformedURLException {
		System.out.println("\nTESTING M-COMMERCE");
		SwishClient client = new SwishClient(true);
		SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(M_COMMERCE));
		System.out.println(response.getLocation());
		System.out.println(response.getPaymentRequestToken());
		testCheckStatus(response.getLocation());
	}
	
	@Test
	public void testDeclinedTransaction() throws MalformedURLException, SwishException {
		System.out.println("\nTESTING DECLINED TRANSACTION");
		SwishClient client = new SwishClient(true);
		try {
			SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(M_COMMERCE, TRANSACTION_DECLINED_ERROR));
		} catch (SwishException e) {
			System.out.println("Request failed succesfully!");
		};
	}
	
	void testCheckStatus(String url) throws SwishException, MalformedURLException {
		SwishClient client = new SwishClient(true);
		SwishPayment payment = client.checkPaymentStatus(new URL(url));
		System.out.println("Status: " + payment.getStatus());
		Assert.assertNotSame("ERROR", payment.getStatus());
	}
	
	PaymentRequest getTestRequest(int type) {
		return getTestRequest(type, null);
	}
	
	PaymentRequest getTestRequest(int type, String error) {
		PaymentRequest request = new PaymentRequest();
		request.setCallbackUrl("https://services.notima.se/broker-rest/ws/payment/swish/callback");
		request.setPayeeAlias("1231181189");
		if(type == E_COMMERCE)
			request.setPayerAlias("46731930431");
		request.setAmount("100");
		if(error != null)
			request.setMessage(error);
		return request;
	}
}
