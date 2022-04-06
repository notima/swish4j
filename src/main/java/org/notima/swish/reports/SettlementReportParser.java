package org.notima.swish.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.notima.swish.internal.HandelsBankenReportParser;
import org.notima.swish.internal.ReportParser;
import org.notima.swish.internal.ReportParserImpl;

/**
 * Parses a settlement report.
 * 
 * @author Oliver Norin
 *
 */
public class SettlementReportParser {

    public SettlementReport parseFile (InputStream inStream) throws IOException, ParseException {        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.ISO_8859_1));
        String firstLine = reader.readLine();
        ReportParser parser = new ReportParserImpl();
        if(firstLine.charAt(0) == '0') {
            parser = new HandelsBankenReportParser();
            ((HandelsBankenReportParser)parser).setSeparator(firstLine.charAt(2));
        }

        return parser.parse(reader);
    }
}
