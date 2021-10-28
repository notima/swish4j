package org.notima.swish.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SettlementReport parseFile (InputStream inStream) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
        SettlementReport report = new SettlementReport();
        report.setRows(new ArrayList<SettlementReportRow>());
        reader.readLine();
        Map<Integer, String> indexMap = getHeaderIndicies(reader.readLine());
        String line = reader.readLine();
        report.setClearingNumber(getValue(line, K_CLEARING_NR, indexMap));
        report.setAccountNumber(getValue(line, K_ACCOUNT_NR, indexMap));
        while(line != null) {
            SettlementReportRow row = parseLine(line, indexMap);
            report.getRows().add(row);
            line = reader.readLine();
        }
        reader.close();
        inStream.close();
        return report;
    }

    private String getValue(String line, String header, Map<Integer, String> indexMap) {
        String[] values = line.split(",");
        for(int i = 0 ; i < values.length; i++) {
            if(indexMap.get(i).equals(header))
                return values[i];
        }
        return null;
    }

    private Map<Integer, String> getHeaderIndicies(String headerLine) {
        Map<Integer, String> indexMap = new HashMap<Integer, String>();
        String[] headers = headerLine.split(",");
        for(int i = 0; i < headers.length; i++) {
            indexMap.put(i, headers[i]);
        }
        return indexMap;
    }

    private SettlementReportRow parseLine(String line, Map<Integer, String> indexMap) throws ParseException {
        SettlementReportRow row = new SettlementReportRow();
        String[] values = line.split(",");
        Date transactionDate = new Date();
        for(int i = 0 ; i < values.length; i++) {

            if(indexMap.get(i).equals(K_BOOK_KEEPING_DATE))
                row.setBookKeepingDate(dateFormat.parse(values[i]));

            if(indexMap.get(i).equals(K_TRANSACTION_DATE)) {
                Date date = dateFormat.parse(values[i]);
                transactionDate.setYear(date.getYear());
                transactionDate.setMonth(date.getMonth());
                transactionDate.setDate(date.getDate());
            }

            if(indexMap.get(i).equals(K_CURRENCY_DATE))
                row.setCurrencyDate(dateFormat.parse(values[i]));

            if(indexMap.get(i).equals(K_RECIPIENT_NR))
                row.setRecipientNumber(values[i]);

            if(indexMap.get(i).equals(K_RECIPIENT_NAME))
                row.setRecipientName(values[i]);

            if(indexMap.get(i).equals(K_SENDER_NR))
                row.setSenderNumber(values[i]);

            if(indexMap.get(i).equals(K_SENDER_NAME))
                row.setSenderName(values[i]);

            if(indexMap.get(i).equals(K_MESSAGE))
                row.setMessage(values[i]);

            if(indexMap.get(i).equals(K_TIME)) {
                int hour = Integer.parseInt(values[i].split(":")[0]);
                int minute = Integer.parseInt(values[i].split(":")[1]);
                transactionDate.setHours(hour);
                transactionDate.setMinutes(minute);
            }

            if(indexMap.get(i).equals(K_AMOUNT))
                row.setAmount(Float.parseFloat(values[i]));

            if(indexMap.get(i).equals(K_ORDER_REF))
                row.setOrderReference(values[i]);
        }
        row.setTransactionDate(transactionDate);
        return row;
    }
}
