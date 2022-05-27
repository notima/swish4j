package org.notima.swish.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.notima.swish.reports.SettlementReport;
import org.notima.swish.reports.SettlementReportRow;

public class HandelsBankenReportParser implements ReportParser {

    private static final String H_RECORD_TYPE = "recType";
    private static final String H_ORG_NO = "orgNo";
    private static final String H_ACCOUNT_NUMBER = "accNo";
    private static final String H_BIC = "bic";
    private static final String H_SWISH_NUMBER = "swishNo";
    private static final String H_TRANSACTION_DATE = "tDate";
    private static final String H_TRANSACTION_TYPE = "tType";
    private static final String H_AMOUNT = "amount";
    private static final String H_CURRENCY = "currency";
    private static final String H_MOBILE_NUMBER = "mobNo";
    private static final String H_NAME = "name";
    private static final String H_PAYMENT_REFERENCE = "pRef";
    private static final String H_NOTIFICATION = "notif";
    private static final String H_ORDER_ID = "orderId";
    private static final String H_TIME = "time";
    private static final String H_BOOKING_DATE = "bookingDate";
    private static final String H_INSTRUCTION_ID = "instructionId";
    private static final String H_E2E_ID = "e2eId";

    private char separator;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat decimalFormat = new DecimalFormat("0.#");

    @Override
    public SettlementReport parse(BufferedReader reader) throws IOException, ParseException {
        SettlementReport report = new SettlementReport();
        report.setRows(new ArrayList<SettlementReportRow>());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
            .withDelimiter(separator)
            .withHeader(
                H_RECORD_TYPE, 
                H_ORG_NO, 
                H_ACCOUNT_NUMBER, 
                H_BIC, 
                H_SWISH_NUMBER, 
                H_TRANSACTION_DATE, 
                H_TRANSACTION_TYPE, 
                H_AMOUNT, 
                H_CURRENCY, 
                H_MOBILE_NUMBER, 
                H_NAME, 
                H_PAYMENT_REFERENCE, 
                H_NOTIFICATION, 
                H_ORDER_ID,
                H_TIME,
                H_BOOKING_DATE,
                H_INSTRUCTION_ID,
                H_E2E_ID
            )
            .parse(reader);

        for (CSVRecord record : records) {
            if(record.size() > 2) {
                report.getRows().add(parseRecord(record));
                report.setAccountNumber(record.get(H_ACCOUNT_NUMBER));
            }
        }
        reader.close();
        return report;
    }

    private SettlementReportRow parseRecord(CSVRecord record) throws ParseException {
        SettlementReportRow row = new SettlementReportRow();
        row.setAmount(decimalFormat.parse(record.get(H_AMOUNT)).doubleValue());
        try {
        	row.setBookKeepingDate(dateFormat.parse(record.get(H_BOOKING_DATE)));
        } catch (java.text.ParseException pe) {
        	// For some reason, this can be empty
        }
        row.setCheckoutOrderId(record.get(H_ORDER_ID));
        row.setMessage(record.get(H_NOTIFICATION));
        row.setRecipientName(record.get(H_ORG_NO));
        row.setRecipientNumber(record.get(H_SWISH_NUMBER));
        row.setSenderName(record.get(H_NAME));
        row.setSenderNumber(record.get(H_MOBILE_NUMBER));
        row.setTransactionDate(dateFormat.parse(record.get(H_TRANSACTION_DATE)));
        return row;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(separator == ',' ? '.' : ',');
        decimalFormat = new DecimalFormat("0.#");
        decimalFormat.setDecimalFormatSymbols(symbols);
    }
    
}
