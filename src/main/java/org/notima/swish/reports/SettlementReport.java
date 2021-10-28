package org.notima.swish.reports;

import java.util.List;

public class SettlementReport {
    private String clearingNumber;
    private String accountNumber;
    private List<SettlementReportRow> rows;

    public String getClearingNumber() {
        return clearingNumber;
    }

    public void setClearingNumber(String clearingNumber) {
        this.clearingNumber = clearingNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public List<SettlementReportRow> getRows() {
        return rows;
    }

    public void setRows(List<SettlementReportRow> rows) {
        this.rows = rows;
    }
}
