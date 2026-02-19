package com.example.eventphoto.service;

import com.example.eventphoto.model.Payment;
import com.example.eventphoto.model.PaymentStatus;
import com.example.eventphoto.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;

    @Transactional
    public void verifyAndCapture(String orderId, String paymentId, String signature) {
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (payment.getStatus() == PaymentStatus.CAPTURED) {
            return;
        }
        boolean valid = razorpayService.verifyPaymentSignature(orderId, paymentId, signature);
        if (valid) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.CAPTURED);
            paymentRepository.save(payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Invalid payment signature");
        }
    }

    public Payment getByOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
