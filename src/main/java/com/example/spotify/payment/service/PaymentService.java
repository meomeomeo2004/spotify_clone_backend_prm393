package com.example.spotify.payment.service;

import com.example.spotify.payment.dto.CreatePaymentRequest;
import com.example.spotify.payment.entity.PaymentTransaction;
import com.example.spotify.payment.repository.PaymentTransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final VnpayService vnpayService;

    public PaymentService(PaymentTransactionRepository paymentTransactionRepository,
                          VnpayService vnpayService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.vnpayService = vnpayService;
    }

    @Transactional
    public Map<String, String> createPayment(CreatePaymentRequest request, String clientIp, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("user_id is required");
        }

        String txnRef = "PM" + System.currentTimeMillis();

        PaymentTransaction tx = new PaymentTransaction();
        tx.setUserId(userId); // <-- FIX QUAN TRỌNG
        tx.setTxnRef(txnRef);
        tx.setPlanId(request.getPlanId());
        tx.setAmount(request.getAmount());
        tx.setStatus("PENDING");
        paymentTransactionRepository.save(tx);

        String paymentUrl = vnpayService.buildPaymentUrl(
                txnRef,
                request.getAmount(),
                request.getOrderInfo(),
                request.getLocale(),
                request.getBankCode(),
                clientIp
        );

        Map<String, String> res = new HashMap<>();
        res.put("paymentUrl", paymentUrl);
        res.put("txnRef", txnRef);
        return res;
    }

    @Transactional
    public void handleVnpReturnOrIpn(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String transactionNo = params.get("vnp_TransactionNo");

        if (txnRef == null || txnRef.isBlank()) return;

        Optional<PaymentTransaction> opt = paymentTransactionRepository.findByTxnRef(txnRef);
        if (opt.isEmpty()) return;

        PaymentTransaction tx = opt.get();
        tx.setVnpResponseCode(responseCode);
        tx.setVnpTransactionStatus(transactionStatus);
        tx.setVnpTransactionNo(transactionNo);

        boolean success = "00".equals(responseCode) && "00".equals(transactionStatus);
        tx.setStatus(success ? "SUCCESS" : "FAILED");

        paymentTransactionRepository.save(tx);
    }

    public Map<String, String> getStatus(String txnRef) {
        Map<String, String> res = new HashMap<>();
        res.put("txnRef", txnRef);

        Optional<PaymentTransaction> opt = paymentTransactionRepository.findByTxnRef(txnRef);
        if (opt.isEmpty()) {
            res.put("status", "pending");
            return res;
        }

        String dbStatus = opt.get().getStatus();
        res.put("status", dbStatus == null ? "pending" : dbStatus.toLowerCase());
        return res;
    }
}