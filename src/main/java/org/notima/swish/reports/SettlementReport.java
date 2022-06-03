package org.notima.swish.reports;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

/**
 * Representation of a Swish settlement report.
 * 
 * @author Daniel Tamm
 * @author Oliver Norin
 *
 */
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
    
    @Transient
    public boolean hasRows() {
    	return rows!=null && !rows.isEmpty();
    }
    
    @Transient
    public Date getFirstTransactionDate() {
    	if (!hasRows()) return null; 

    	Date firstDate = new Date(Long.MAX_VALUE);
    	
    	for (SettlementReportRow r : rows) {
    		if (r.getTransactionDate().before(firstDate)) {
    				firstDate = r.getTransactionDate();
    		}
    	}
    	
    	return firstDate;
    }
    
    @Transient
    public Date getLastTransactionDate() {
    	if (!hasRows()) return null; 

    	Date lastDate = new Date(0);
    	
    	for (SettlementReportRow r : rows) {
    		if (r.getTransactionDate().after(lastDate)) {
    				lastDate = r.getTransactionDate();
    		}
    	}
    	
    	return lastDate;
    }
    
    
    
    
}
