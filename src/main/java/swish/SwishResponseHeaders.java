package swish;

public class SwishResponseHeaders {
	private String location;
	private String paymentRequestToken;
	
	public SwishResponseHeaders() {}
	
	public SwishResponseHeaders(String location, String paymentRequestToken) {
		this.location = location;
		this.paymentRequestToken = paymentRequestToken;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPaymentRequestToken() {
		return paymentRequestToken;
	}
	public void setPaymentRequestToken(String paymentRequestToken) {
		this.paymentRequestToken = paymentRequestToken;
	}
}
