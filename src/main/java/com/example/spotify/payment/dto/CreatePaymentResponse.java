package com.example.spotify.payment.dto;

public class CreatePaymentResponse {
    private String paymentUrl;
    private String txnRef;

    public CreatePaymentResponse(String paymentUrl, String txnRef) {
        this.paymentUrl = paymentUrl;
        this.txnRef = txnRef;
    }

    public String getPaymentUrl() { return paymentUrl; }
    public String getTxnRef() { return txnRef; }
}
