package com.example.spotify.payment.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    /**
     * Build payment URL for VNPAY (v2.1.0)
     */
    public String buildPaymentUrl(
            String txnRef,
            Long amountVnd,
            String orderInfo,
            String locale,
            String bankCode,
            String clientIp
    ) {
        try {
            Map<String, String> params = new HashMap<>();

            // Required fields
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", tmnCode);
            params.put("vnp_Amount", String.valueOf(amountVnd * 100)); // VNPAY requires x100
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", txnRef);
            params.put("vnp_OrderInfo", safe(orderInfo, "Thanh toan goi Premium"));
            params.put("vnp_OrderType", "other");
            params.put("vnp_Locale", (locale == null || locale.isBlank()) ? "vn" : locale);
            params.put("vnp_ReturnUrl", returnUrl);
            params.put("vnp_IpAddr", normalizeIp(clientIp));
            params.put("vnp_CreateDate", now("yyyyMMddHHmmss"));
            params.put("vnp_ExpireDate", plusMinutes(15, "yyyyMMddHHmmss"));

            // Optional
            if (bankCode != null && !bankCode.isBlank()) {
                params.put("vnp_BankCode", bankCode);
            }

            // Build hashData + query
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (int i = 0; i < fieldNames.size(); i++) {
                String key = fieldNames.get(i);
                String value = params.get(key);

                if (value != null && !value.isEmpty()) {
                    String encodedValue = URLEncoder.encode(value, StandardCharsets.US_ASCII);

                    hashData.append(key).append('=').append(encodedValue);
                    query.append(key).append('=').append(encodedValue);

                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String secureHash = hmacSHA512(hashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            return payUrl + "?" + query;
        } catch (Exception e) {
            throw new RuntimeException("Cannot build VNPAY payment url", e);
        }
    }

    /**
     * Verify callback/IPN signature from VNPAY
     */
    public boolean verifySignature(Map<String, String> inputParams) {
        try {
            Map<String, String> params = new HashMap<>(inputParams);

            String receivedHash = params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            if (receivedHash == null || receivedHash.isBlank()) {
                return false;
            }

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            boolean first = true;

            for (String key : fieldNames) {
                String value = params.get(key);
                if (value != null && !value.isEmpty()) {
                    if (!first) hashData.append('&');
                    hashData.append(key)
                            .append('=')
                            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                    first = false;
                }
            }

            String calculated = hmacSHA512(hashSecret, hashData.toString());
            return calculated.equalsIgnoreCase(receivedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String now(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    private String plusMinutes(int minutes, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minutes);
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String normalizeIp(String ip) {
        if (ip == null || ip.isBlank()) return "127.0.0.1";
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) return "127.0.0.1";
        return ip;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hash = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }
}
