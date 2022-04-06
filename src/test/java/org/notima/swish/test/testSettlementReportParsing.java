package org.notima.swish.test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;
import org.notima.swish.reports.SettlementReport;
import org.notima.swish.reports.SettlementReportParser;
import org.notima.swish.reports.SettlementReportRow;

public class testSettlementReportParsing {
    
    @Test
    public void testParseSettlementReport() throws IOException, ParseException {
        File[] reportFiles = {
            new File("src/test/resources/Swishrapport.csv"),
            new File("src/test/resources/Handelsbankenrapport1.csv"),
            new File("src/test/resources/Handelsbankenrapport2.csv")
        };
        for(File reportFile : reportFiles) {
            System.out.println(reportFile.getName());
            FileInputStream reportInputStream = new FileInputStream(reportFile);
            SettlementReport report = new SettlementReportParser().parseFile(reportInputStream);
            reportInputStream.close();
            for(SettlementReportRow row : report.getRows()) {
                System.out.printf("%s,\t%s,\t%s,\t%s,\t%f\t%s\n", row.getSenderName(), row.getSenderNumber(), row.getRecipientName(), row.getRecipientNumber(), row.getAmount(), row.getTransactionDate().toString());
            }
        }
    }
}
