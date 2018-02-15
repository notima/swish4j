package swish;

import java.util.Date;

public class PaymentResponse extends PaymentRequest {
	public static final String CREATED = "CREATED";
	public static final String PAID = "PAID";
	public static final String DECLINED = "DECLINED";
	public static final String ERROR = "ERROR";
	
	/**
	 * Payment request ID
	 */
	private String id;
	
	/**
	 * Payment reference, from the bank, of the payment that
	 * occurred based on the Payment request. Only available 
	 * if status is PAID.
	 */
	private String paymentReference;
	
	/**
	 * The status of the transaction. Possible values:
	 *  CREATED, PAID, DECLINED, ERROR.
	 */
	private String status;
	
	/**
	 * The time and date that the payment request was
	 * created.
	 */
	private Date dateCreated;
	
	/**
	 * The time and date that the payment request was paid.
	 * Only applicable if status was PAID.
	 */
	private Date datePaid;
	
	/**
	 * A code indicating what type of error occurred. Only 
	 * applicable if status is ERROR.
	 */
	private String errorCode;
	
	/**
	 * A descriptive error message (in English) indicating
	 * what type of error occurred. Only applicable if
	 * status is ERROR
	 */
	private String errorMessage;
	
	/**
	 * Additional information about the error. Only applicable if
	 * status is ERROR.
	 */
	private String additionalInformation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(Date datePaid) {
		this.datePaid = datePaid;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
}
