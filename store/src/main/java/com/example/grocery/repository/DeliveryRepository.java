package com.example.grocery.repository;

import com.example.grocery.model.Delivery;
import com.example.grocery.model.DeliveryStatus;
import com.example.grocery.model.ExpressDelivery;
import com.example.grocery.model.StandardDelivery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DeliveryRepository {

    public DeliveryRepository() {
        FileUtil.ensureBaseFiles();
    }

    public List<Delivery> findAll() {
        List<Delivery> deliveries = new ArrayList<>();
        for (String line : FileUtil.readAllLines(FileUtil.DELIVERIES_FILE)) {
            Delivery delivery = parse(line);
            if (delivery != null) {
                deliveries.add(delivery);
            }
        }
        return deliveries;
    }

    public Optional<Delivery> findById(String id) {
        return findAll().stream().filter(d -> d.getId().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<Delivery> findByOrderId(String orderId) {
        return findAll().stream().filter(d -> d.getOrderId().equalsIgnoreCase(orderId)).findFirst();
    }

    public Delivery save(Delivery delivery) {
        List<Delivery> deliveries = findAll();
        if (delivery.getId() == null || delivery.getId().isBlank()) {
            List<String> ids = deliveries.stream().map(Delivery::getId).collect(Collectors.toList());
            delivery.setId(FileUtil.nextId("DLV", ids));
        }
        deliveries.add(delivery);
        writeAll(deliveries);
        return delivery;
    }

    public boolean update(Delivery updatedDelivery) {
        List<Delivery> deliveries = findAll();
        boolean replaced = false;
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).getId().equalsIgnoreCase(updatedDelivery.getId())) {
                deliveries.set(i, updatedDelivery);
                replaced = true;
                break;
            }
        }
        if (replaced) {
            writeAll(deliveries);
        }
        return replaced;
    }

    private void writeAll(List<Delivery> deliveries) {
        List<String> lines = deliveries.stream().map(this::toLine).collect(Collectors.toList());
        FileUtil.writeAllLines(FileUtil.DELIVERIES_FILE, lines);
    }

    private Delivery parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) {
            return null;
        }

        String type = parts[3];
        Delivery delivery = "EXPRESS".equalsIgnoreCase(type) ? new ExpressDelivery() : new StandardDelivery();
        delivery.setId(parts[0]);
        delivery.setOrderId(parts[1]);
        delivery.setCustomerId(parts[2]);
        delivery.setAssignedPerson(parts[4]);
        delivery.setStatus(parseStatus(parts[5]));
        delivery.setFee(FileUtil.parseDouble(parts[6], 0));
        delivery.setEstimatedHours(FileUtil.parseInt(parts[7], 0));
        try {
            delivery.setCreatedAt(LocalDateTime.parse(parts[8]));
        } catch (Exception ex) {
            delivery.setCreatedAt(LocalDateTime.now());
        }
        return delivery;
    }

    private DeliveryStatus parseStatus(String value) {
        try {
            return DeliveryStatus.valueOf(value);
        } catch (Exception ex) {
            return DeliveryStatus.SCHEDULED;
        }
    }

    private String toLine(Delivery delivery) {
        return String.join(FileUtil.DELIMITER,
                FileUtil.safe(delivery.getId()),
                FileUtil.safe(delivery.getOrderId()),
                FileUtil.safe(delivery.getCustomerId()),
                delivery.getDeliveryType(),
                FileUtil.safe(delivery.getAssignedPerson()),
                delivery.getStatus().name(),
                String.valueOf(delivery.getFee()),
                String.valueOf(delivery.getEstimatedHours()),
                delivery.getCreatedAt().toString()
        );
    }
}
