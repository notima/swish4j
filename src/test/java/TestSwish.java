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
	
	@Test
	public void testPaymentRequestECommerce() throws SwishException, MalformedURLException {
		System.out.println("TESTING E-COMMERCE");
		SwishClient client = new SwishClient(true);
		SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(E_COMMERCE));
		System.out.println(response.getLocation());
		testCheckStatus(response.getLocation());
	}
	
	@Test
	public void testPaymentRequestMCommerce() throws SwishException, MalformedURLException {
		System.out.println("TESTING M-COMMERCE");
		SwishClient client = new SwishClient(true);
		SwishResponseHeaders response = client.sendPaymentRequest(getTestRequest(M_COMMERCE));
		System.out.println(response.getLocation());
		System.out.println(response.getPaymentRequestToken());
		testCheckStatus(response.getLocation());
	}
	
	void testCheckStatus(String url) throws SwishException, MalformedURLException {
		SwishClient client = new SwishClient(true);
		SwishPayment payment = client.checkPaymentStatus(new URL(url));
		System.out.println("Status: " + payment.getStatus());
		Assert.assertNotSame("ERROR", payment.getStatus());
	}
	
	PaymentRequest getTestRequest(int type) {
		PaymentRequest request = new PaymentRequest();
		request.setCallbackUrl("https://services.notima.se/parkado-rest/ws/payment/swish-callback");
		request.setPayeeAlias("1231181189");
		if(type == E_COMMERCE)
			request.setPayerAlias("46731930431");
		request.setAmount("100");
		return request;
	}
}
