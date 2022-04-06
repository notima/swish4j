package org.notima.swish.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

import org.notima.swish.reports.SettlementReport;

public interface ReportParser {
    public SettlementReport parse (BufferedReader reader) throws IOException, ParseException;
}
