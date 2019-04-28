package com.cniupay.pay.common;

public class PayResult {

    private String tradeNo;
    private Integer payType;
    private String realTradeNo;
    private Integer status;
    private Long amount;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getRealTradeNo() {
        return realTradeNo;
    }

    public void setRealTradeNo(String realTradeNo) {
        this.realTradeNo = realTradeNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
