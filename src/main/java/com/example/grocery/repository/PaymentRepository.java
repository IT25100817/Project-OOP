package com.example.grocery.repository;

import com.example.grocery.model.CashOnDelivery;
import com.example.grocery.model.OnlinePayment;
import com.example.grocery.model.Payment;
import com.example.grocery.model.PaymentStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentRepository {

    public PaymentRepository() {
        FileUtil.ensureBaseFiles();
    }

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        for (String line : FileUtil.readAllLines(FileUtil.PAYMENTS_FILE)) {
            Payment payment = parse(line);
            if (payment != null) {
                payments.add(payment);
            }
        }
        return payments;
    }

    public Optional<Payment> findById(String id) {
        return findAll().stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Payment> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(payment -> payment.getCustomerId().equalsIgnoreCase(customerId))
                .collect(Collectors.toList());
    }

    public Payment save(Payment payment) {
        List<Payment> payments = findAll();
        if (payment.getId() == null || payment.getId().isBlank()) {
            List<String> ids = payments.stream().map(Payment::getId).collect(Collectors.toList());
            payment.setId(FileUtil.nextId("PAY", ids));
        }
        payments.add(payment);
        writeAll(payments);
        return payment;
    }

    public boolean update(Payment updatedPayment) {
        List<Payment> payments = findAll();
        boolean replaced = false;
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getId().equalsIgnoreCase(updatedPayment.getId())) {
                payments.set(i, updatedPayment);
                replaced = true;
                break;
            }
        }
        if (replaced) {
            writeAll(payments);
        }
        return replaced;
    }

    private void writeAll(List<Payment> payments) {
        List<String> lines = payments.stream().map(this::toLine).collect(Collectors.toList());
        FileUtil.writeAllLines(FileUtil.PAYMENTS_FILE, lines);
    }

    private Payment parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            return null;
        }

        String type = parts[3];
        Payment payment = "ONLINE".equalsIgnoreCase(type) ? new OnlinePayment() : new CashOnDelivery();
        payment.setId(parts[0]);
        payment.setOrderId(parts[1]);
        payment.setCustomerId(parts[2]);
        payment.setAmount(FileUtil.parseDouble(parts[4], 0));
        payment.setStatus(parseStatus(parts[5]));
        payment.setReference(parts[6]);
        try {
            payment.setCreatedAt(LocalDateTime.parse(parts[7]));
        } catch (Exception ex) {
            payment.setCreatedAt(LocalDateTime.now());
        }

        return payment;
    }

    private PaymentStatus parseStatus(String value) {
        try {
            return PaymentStatus.valueOf(value);
        } catch (Exception ex) {
            return PaymentStatus.PENDING;
        }
    }

    private String toLine(Payment payment) {
        return String.join(FileUtil.DELIMITER,
                FileUtil.safe(payment.getId()),
                FileUtil.safe(payment.getOrderId()),
                FileUtil.safe(payment.getCustomerId()),
                payment.getPaymentType(),
                String.valueOf(payment.getAmount()),
                payment.getStatus().name(),
                FileUtil.safe(payment.getReference()),
                payment.getCreatedAt().toString()
        );
    }
}
