package com.example.eventphoto.controller;

import com.example.eventphoto.dto.ApiResponse;
import com.example.eventphoto.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> callback(
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature) {
        paymentService.verifyAndCapture(orderId, paymentId, signature);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", null));
    }
}
