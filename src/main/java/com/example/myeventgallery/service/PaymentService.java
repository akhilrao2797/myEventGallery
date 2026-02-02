package com.example.myeventgallery.service;

import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.PricingPackage;
import com.example.myeventgallery.model.Payment;
import com.example.myeventgallery.model.PaymentStatus;
import com.example.myeventgallery.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Transactional
    public Payment createPayment(Event event, PricingPackage packagePlan) {
        BigDecimal baseAmount = packagePlan.getBasePrice();
        
        // Calculate extra guest charges if applicable
        if (event.getExpectedGuests() != null && packagePlan.getMaxGuests() != null) {
            int extraGuests = Math.max(0, event.getExpectedGuests() - packagePlan.getMaxGuests());
            if (extraGuests > 0 && packagePlan.getPricePerExtraGuest() != null) {
                BigDecimal extraGuestCharge = packagePlan.getPricePerExtraGuest()
                        .multiply(new BigDecimal(extraGuests));
                baseAmount = baseAmount.add(extraGuestCharge);
            }
        }
        
        // Calculate S3 cost (estimated)
        BigDecimal s3Cost = calculateS3Cost(packagePlan);
        
        // Calculate company profit
        BigDecimal companyProfitPercentage = packagePlan.getCompanyProfitPercentage()
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal companyProfit = baseAmount.multiply(companyProfitPercentage);
        
        // Total amount
        BigDecimal totalAmount = baseAmount.add(s3Cost);
        
        Payment payment = new Payment();
        payment.setEvent(event);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setAmount(totalAmount);
        payment.setBaseAmount(baseAmount);
        payment.setCompanyProfit(companyProfit);
        payment.setS3Cost(s3Cost);
        payment.setStatus(PaymentStatus.PENDING);
        
        return paymentRepository.save(payment);
    }
    
    private BigDecimal calculateS3Cost(PricingPackage packagePlan) {
        // AWS S3 pricing: ~$0.023 per GB per month
        // This is a simplified calculation
        if (packagePlan.getStorageGB() != null) {
            BigDecimal pricePerGB = new BigDecimal("0.023");
            int months = packagePlan.getStorageDays() / 30;
            return pricePerGB.multiply(new BigDecimal(packagePlan.getStorageGB()))
                    .multiply(new BigDecimal(months))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(status);
        if (status == PaymentStatus.COMPLETED) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        return paymentRepository.save(payment);
    }
    
    public Payment getPaymentByEventId(Long eventId) {
        return paymentRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
