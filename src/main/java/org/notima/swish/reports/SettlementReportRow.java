package org.notima.swish.reports;

import java.util.Date;

public class SettlementReportRow {

    private Date bookKeepingDate;
    private Date transactionDate;
    private Date currencyDate;
    private String recipientNumber;
    private String recipientName;
    private String senderNumber;
    private String senderName;
    private String message;
    private float amount;
    private String orderReference;

    public Date getBookKeepingDate() {
        return bookKeepingDate;
    }

    public void setBookKeepingDate(Date bookKeepingDate) {
        this.bookKeepingDate = bookKeepingDate;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getCurrencyDate() {
        return currencyDate;
    }

    public void setCurrencyDate(Date currencyDate) {
        this.currencyDate = currencyDate;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public void setPaymentDate(Date parse) {
    }

}
