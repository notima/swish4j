import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;
import org.notima.swish.reports.SettlementReport;
import org.notima.swish.reports.SettlementReportParser;
import org.notima.swish.reports.SettlementReportRow;

public class testSettlementReportParsing {
    
    @Test
    public void testParseSettlementReport() throws IOException, ParseException {
        File reportFile = new File("src/test/resources/Swishrapport.csv");
        FileInputStream reportInputStream = new FileInputStream(reportFile);
        SettlementReport report = new SettlementReportParser().parseFile(reportInputStream);
        reportInputStream.close();
        assertEquals("54321", report.getClearingNumber());
        assertEquals("1029384756", report.getAccountNumber());
        for(SettlementReportRow row : report.getRows()) {
            System.out.printf("%s,\t%s,\t%s,\t%s,\t%f\t%s\n", row.getSenderName(), row.getSenderNumber(), row.getRecipientName(), row.getRecipientNumber(), row.getAmount(), row.getTransactionDate().toString());
        }
    }
}
