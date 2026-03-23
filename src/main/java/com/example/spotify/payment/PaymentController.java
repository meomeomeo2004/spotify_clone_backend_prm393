package com.example.spotify.payment;

import com.example.spotify.payment.dto.CreatePaymentRequest;
import com.example.spotify.payment.service.PaymentService;
import com.example.spotify.payment.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final VnpayService vnpayService;
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService, VnpayService vnpayService) {
        this.paymentService = paymentService;
        this.vnpayService = vnpayService;
    }

    @PostMapping("/vnpay/create")
    public ResponseEntity<?> create(@RequestBody CreatePaymentRequest request,
                                    HttpServletRequest httpRequest,
                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            String ip = httpRequest.getRemoteAddr();

            // KHÔNG fallback user mặc định
            if (userId == null) {
                return ResponseEntity.status(401).body(
                        Map.of("message", "Unauthorized: missing X-User-Id")
                );
            }

            return ResponseEntity.ok(paymentService.createPayment(request, ip, userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of("message", "create payment failed", "error", e.getMessage())
            );
        }
    }

    @GetMapping("/{txnRef}/status")
    public Map<String, String> status(@PathVariable String txnRef) {
        return paymentService.getStatus(txnRef);
    }

    @GetMapping("/vnpay-return")
    public void vnpReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = extractAll(request);
        boolean validSignature = vnpayService.verifySignature(params);

        if (validSignature) {
            paymentService.handleVnpReturnOrIpn(params);
        }

        String txnRef = params.getOrDefault("vnp_TxnRef", "");
        String responseCode = params.getOrDefault("vnp_ResponseCode", "");
        String transactionStatus = params.getOrDefault("vnp_TransactionStatus", "");
        boolean success = validSignature && "00".equals(responseCode) && "00".equals(transactionStatus);

        String deepLink = "myspotify://payment-result?status=" + (success ? "success" : "failed") + "&txnRef=" + txnRef;
        response.sendRedirect(deepLink);
    }

    @GetMapping("/vnpay-ipn")
    public Map<String, String> ipn(HttpServletRequest request) {
        Map<String, String> params = extractAll(request);
        boolean validSignature = vnpayService.verifySignature(params);

        if (!validSignature) {
            Map<String, String> ack = new HashMap<>();
            ack.put("RspCode", "97");
            ack.put("Message", "Invalid Checksum");
            return ack;
        }

        paymentService.handleVnpReturnOrIpn(params);

        Map<String, String> ack = new HashMap<>();
        ack.put("RspCode", "00");
        ack.put("Message", "Confirm Success");
        return ack;
    }

    @GetMapping("/vnpay/manual-confirm")
    public Map<String, String> manualConfirm(@RequestParam Map<String, String> params) {
        boolean valid = vnpayService.verifySignature(params);
        if (!valid) {
            return Map.of("ok", "false", "message", "Invalid signature");
        }

        paymentService.handleVnpReturnOrIpn(params);
        return Map.of("ok", "true", "txnRef", params.getOrDefault("vnp_TxnRef", ""));
    }

    private Map<String, String> extractAll(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) map.put(k, v[0]);
        });
        return map;
    }
}