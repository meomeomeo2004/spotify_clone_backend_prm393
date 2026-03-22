package com.example.spotify.payment.dto;

public class CreatePaymentRequest {
    private String planId;
    private Long amount;
    private String orderInfo;
    private String locale;
    private String bankCode;

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
}
