package com.example.grocery.service;

import com.example.grocery.model.Delivery;
import com.example.grocery.model.DeliveryStatus;
import com.example.grocery.model.ExpressDelivery;
import com.example.grocery.model.Order;
import com.example.grocery.model.StandardDelivery;
import com.example.grocery.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public Delivery createDeliveryForOrder(Order order) {
        Optional<Delivery> existing = deliveryRepository.findByOrderId(order.getId());
        if (existing.isPresent()) {
            return existing.get();

        }

        Delivery delivery = "EXPRESS".equalsIgnoreCase(order.getOrderType())
                ? new ExpressDelivery()
                : new StandardDelivery();

        delivery.setOrderId(order.getId());
        delivery.setCustomerId(order.getCustomerId());
        delivery.setAssignedPerson("Not Assigned");
        delivery.setStatus(DeliveryStatus.SCHEDULED);
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setFee(delivery.calculateDeliveryFee(order.getSubtotal()));
        delivery.setEstimatedHours(delivery.estimateDeliveryHours());

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Optional<Delivery> getById(String id) {
        return deliveryRepository.findById(id);
    }

    public boolean updateDelivery(String deliveryId, String assignedPerson, String statusText) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (assignedPerson != null && !assignedPerson.isBlank()) {
            delivery.setAssignedPerson(assignedPerson);
        }
        delivery.setStatus(parseStatus(statusText));

        return deliveryRepository.update(delivery);
    }

    private DeliveryStatus parseStatus(String statusText) {
        try {
            return DeliveryStatus.valueOf(statusText.toUpperCase());
        } catch (Exception ex) {
            return DeliveryStatus.SCHEDULED;
        }
    }
}
