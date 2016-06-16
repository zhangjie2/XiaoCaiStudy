package com.xiaocai.qunaer.vo;

import java.math.BigDecimal;

public class RepaymentVo4Qunaer {
    private String loanNo;
    
    private BigDecimal totalamout;
    
    private BigDecimal capital;
    
    private BigDecimal fee;
    
    private BigDecimal fine;
    
    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public BigDecimal getTotalamout() {
        return totalamout;
    }

    public void setTotalamout(BigDecimal totalamout) {
        this.totalamout = totalamout;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }
    
   
}
