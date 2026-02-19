package com.example.eventphoto.service;

import com.example.eventphoto.model.PaymentStatus;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class RazorpayService {

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    private static final String CURRENCY_INR = "INR";

    public OrderCreateResult createOrder(BigDecimal amountPaise, String receipt) {
        if (keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
            log.warn("Razorpay credentials not set; using stub order");
            return OrderCreateResult.builder()
                    .orderId("stub_order_" + System.currentTimeMillis())
                    .amount(amountPaise)
                    .currency(CURRENCY_INR)
                    .build();
        }
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountPaise.longValue());
            orderRequest.put("currency", CURRENCY_INR);
            orderRequest.put("receipt", receipt != null ? receipt : "rcpt_" + System.currentTimeMillis());
            Order order = client.orders.create(orderRequest);
            String orderId = order != null && order.get("id") != null ? String.valueOf(order.get("id")) : "order_" + System.currentTimeMillis();
            return OrderCreateResult.builder()
                    .orderId(orderId)
                    .amount(amountPaise)
                    .currency(CURRENCY_INR)
                    .build();
        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed", e);
            throw new RuntimeException("Payment initiation failed: " + e.getMessage());
        }
    }

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        if (keySecret == null || keySecret.isBlank()) return true;
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject payload = new JSONObject();
            payload.put("order_id", orderId);
            payload.put("payment_id", paymentId);
            return com.razorpay.Utils.verifyPaymentSignature(payload, signature);
        } catch (RazorpayException e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class OrderCreateResult {
        private String orderId;
        private BigDecimal amount;
        private String currency;
    }
}
