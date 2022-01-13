package org.notima.swish.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Parses a settlement report.
 * 
 * @author Oliver Norin
 *
 */
public class SettlementReportParser {

    private static final String K_CLEARING_NR = "Clnr";
    private static final String K_ACCOUNT_NR = "Kontonr";
    private static final String K_BOOK_KEEPING_DATE = "Bokfdag";
    private static final String K_TRANSACTION_DATE = "Transdag";
    private static final String K_CURRENCY_DATE = "Valutadag";
    private static final String K_RECIPIENT_NR = "Mottagarnr";
    private static final String K_RECIPIENT_NAME = "Mottagarnamn";
    private static final String K_SENDER_NR = "Avsändarnr";
    private static final String K_SENDER_NAME = "Avsändarnamn";
    private static final String K_MESSAGE = "Meddelande";
    private static final String K_TIME = "Tid";
    private static final String K_AMOUNT = "Belopp";
    private static final String K_ORDER_REF = "Orderreferens";
    private static final String K_CHECKOUT_ORDER_ID = "CheckoutOrderId";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SettlementReport parseFile (InputStream inStream) throws IOException, ParseException {        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.ISO_8859_1));
        SettlementReport report = new SettlementReport();
        report.setRows(new ArrayList<SettlementReportRow>());
        reader.readLine();
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            report.getRows().add(parseRecord(record));
            report.setAccountNumber(record.get(K_ACCOUNT_NR));
            report.setClearingNumber(record.get(K_CLEARING_NR));
        }
        reader.close();
        inStream.close();
        return report;
    }

    private SettlementReportRow parseRecord(CSVRecord record) throws ParseException {
        SettlementReportRow row = new SettlementReportRow();
        row.setBookKeepingDate(dateFormat.parse(record.get(K_BOOK_KEEPING_DATE)));
        Date transactionDate = dateFormat.parse(record.get(K_TRANSACTION_DATE));
        String transactionTimeStr = record.get(K_TIME);
        int hour = Integer.parseInt(transactionTimeStr.split(":")[0]);
        int minute = Integer.parseInt(transactionTimeStr.split(":")[1]);
        transactionDate.setHours(hour);
        transactionDate.setMinutes(minute);
        row.setTransactionDate(transactionDate);
        row.setCurrencyDate(dateFormat.parse(record.get(K_CURRENCY_DATE)));
        row.setRecipientNumber(record.get(K_RECIPIENT_NR));
        row.setRecipientName(record.get(K_RECIPIENT_NAME));
        row.setSenderNumber(record.get(K_SENDER_NR));
        row.setSenderName(record.get(K_SENDER_NAME));
        row.setMessage(record.get(K_MESSAGE));
        row.setAmount(Float.parseFloat(record.get(K_AMOUNT)));
        row.setOrderReference(record.get(K_ORDER_REF));
        if(record.isMapped(K_CHECKOUT_ORDER_ID))
            row.setCheckoutOrderId(record.get(K_CHECKOUT_ORDER_ID));
        return row;
    }
}
