package com.example.grocery.service;

import com.example.grocery.model.CashOnDelivery;
import com.example.grocery.model.OnlinePayment;
import com.example.grocery.model.Order;
import com.example.grocery.model.Payment;
import com.example.grocery.model.PaymentStatus;
import com.example.grocery.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPaymentForOrder(Order order, String paymentType, String paymentReference) {
        Payment payment;
        if ("ONLINE".equalsIgnoreCase(paymentType)) {
            String ref = (paymentReference == null || paymentReference.isBlank())
                    ? "TXN-" + System.currentTimeMillis()
                    : paymentReference;
            payment = new OnlinePayment(null, order.getId(), order.getCustomerId(), order.getTotal(),
                    PaymentStatus.PENDING, ref, LocalDateTime.now());
        } else {
            payment = new CashOnDelivery(null, order.getId(), order.getCustomerId(), order.getTotal(),
                    PaymentStatus.PENDING, "COD", LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    public Optional<Payment> getById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public boolean updateStatus(String paymentId, String statusText) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(parseStatus(statusText));
        return paymentRepository.update(payment);
    }

    public String generateReceipt(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return payment.generateReceipt();
    }

    private PaymentStatus parseStatus(String statusText) {
        try {
            return PaymentStatus.valueOf(statusText.toUpperCase());
        } catch (Exception ex) {
            return PaymentStatus.PENDING;
        }
    }
}
