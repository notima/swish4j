package swish;

public class PaymentRequest {
	/**
	 * Payment reference of the payee, which is
	 * the Merchant that receives the payment.
	 * This reference could be order id or similar.
	 * (Optional)
	 */
	private String payeePaymentReference;
	
	/**
	 * URL that Swish will use to notify caller
	 * about the outcome of the Payment request.
	 * The URL has to use HTTPS.
	 * (Mandatory)
	 */
	private String callbackUrl;
	
	/**
	 * The registered Cell phone number of the
	 * person that makes the payment. It can only
	 * contain numbers and has to be at least 8
	 * and at most 15 numbers. It also needs to
	 * match the following format in order to be
	 * found in Swish: country code + cell phone
	 * number (without leading zero). E.g.:
	 * 46712345678
	 * (Optional)
	 */
	private String payerAlias;
	
	/**
	 * The Swish number of the payee. It needs
	 * to match with Merchant Swish number.
	 * (Mandatory)
	 */
	private String payeeAlias = "1231181189";
	
	/**
	 * The amount of money to pay. The amount
	 * cannot be less than 1 SEK and not more
	 * than 999999999999.99 SEK. Valid value 
	 * has to be all numbers or with 2 digit 
	 * decimal
	 * (Mandatory)
	 */
	private String amount;
	
	/**
	 * The currency to use. Only supported value
	 * currently is SEK.
	 * (Mandatory)
	 */
	private String currency = "SEK";
	
	/**
	 * Merchant supplied message about the
	 * payment/order. Max 50 chars. Allowed
	 * characters are the letters a-ö, A-Ö, the
	 * numbers
	 * 0-9 and the special characters :;.,?!()”.
	 * For MSS, errorCode as defined in section 0
	 * can be set in the message property in order
	 * to simulate negative
	 * (Optional)
	 */
	private String message;

	public String getPayeePaymentReference() {
		return payeePaymentReference;
	}

	public void setPayeePaymentReference(String payeePaymentReference) {
		this.payeePaymentReference = payeePaymentReference;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getPayerAlias() {
		return payerAlias;
	}

	public void setPayerAlias(String payerAlias) {
		this.payerAlias = payerAlias;
	}

	public String getPayeeAlias() {
		return payeeAlias;
	}

	public void setPayeeAlias(String payeeAlias) {
		this.payeeAlias = payeeAlias;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
