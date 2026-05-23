package com.example.grocery.repository;

import com.example.grocery.model.Order;
import com.example.grocery.model.OrderItem;
import com.example.grocery.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Inheritance: by annotating with @Repository, this class inherits Spring's
// data-access behaviour (exception translation, bean registration, etc.)
@Repository
public class OrderRepository {

    public OrderRepository() {
        FileUtil.ensureBaseFiles();
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        for (String line : FileUtil.readAllLines(FileUtil.ORDERS_FILE)) {
            Order order = parse(line);
            if (order != null) {
                orders.add(order);
            }
        }
        return orders;
    }

    public Optional<Order> findById(String id) {
        return findAll().stream().filter(o -> o.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Order> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(o -> o.getCustomerId().equalsIgnoreCase(customerId))
                .collect(Collectors.toList());
    }

    // Returns the total number of orders saved in the system
    public int count() {
        return findAll().size();
    }

    public Order save(Order order) {
        List<Order> orders = findAll();
        if (order.getId() == null || order.getId().isBlank()) {
            List<String> ids = orders.stream().map(Order::getId).collect(Collectors.toList());
            order.setId(FileUtil.nextId("ORD", ids));
        }
        orders.add(order);
        writeAll(orders);
        return order;
    }

    public boolean update(Order updatedOrder) {
        List<Order> orders = findAll();
        boolean replaced = false;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equalsIgnoreCase(updatedOrder.getId())) {
                orders.set(i, updatedOrder);
                replaced = true;
                break;
            }
        }
        if (replaced) {
            writeAll(orders);
        }
        return replaced;
    }

    public boolean deleteById(String id) {
        List<Order> orders = findAll();
        boolean removed = orders.removeIf(order -> order.getId().equalsIgnoreCase(id));
        if (removed) {
            writeAll(orders);
        }
        return removed;
    }

    private void writeAll(List<Order> orders) {
        List<String> lines = orders.stream().map(this::toLine).collect(Collectors.toList());
        FileUtil.writeAllLines(FileUtil.ORDERS_FILE, lines);
    }

    private Order parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 12) {
            return null;
        }

        Order order = new Order();
        order.setId(parts[0]);
        order.setCustomerId(parts[1]);
        order.setCustomerName(parts[2]);
        order.setOrderType(parts[3]);
        order.setItems(parseItems(parts[4]));
        order.setSubtotal(FileUtil.parseDouble(parts[5], 0));
        order.setDiscount(FileUtil.parseDouble(parts[6], 0));
        order.setDeliveryFee(FileUtil.parseDouble(parts[7], 0));
        order.setTotal(FileUtil.parseDouble(parts[8], 0));
        order.setStatus(parseStatus(parts[9]));
        try {
            order.setCreatedAt(LocalDateTime.parse(parts[10]));
        } catch (Exception ex) {
            order.setCreatedAt(LocalDateTime.now());
        }
        order.setDispatched(Boolean.parseBoolean(parts[11]));
        return order;
    }

    private OrderStatus parseStatus(String value) {
        try {
            return OrderStatus.valueOf(value);
        } catch (Exception ex) {
            return OrderStatus.PENDING;
        }
    }

    private List<OrderItem> parseItems(String value) {
        List<OrderItem> items = new ArrayList<>();
        if (value == null || value.isBlank()) {
            return items;
        }
        String[] segments = value.split(";");
        for (String segment : segments) {
            String[] fields = segment.split(",", -1);
            if (fields.length < 4) {
                continue;
            }
            items.add(new OrderItem(
                    fields[0],
                    fields[1],
                    FileUtil.parseDouble(fields[2], 0),
                    FileUtil.parseInt(fields[3], 0)
            ));
        }
        return items;
    }

    private String serializeItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> FileUtil.safe(item.getProductId()) + ","
                        + FileUtil.safe(item.getProductName()) + ","
                        + item.getUnitPrice() + ","
                        + item.getQuantity())
                .collect(Collectors.joining(";"));
    }

    private String toLine(Order order) {
        return String.join(FileUtil.DELIMITER,
                FileUtil.safe(order.getId()),
                FileUtil.safe(order.getCustomerId()),
                FileUtil.safe(order.getCustomerName()),
                FileUtil.safe(order.getOrderType()),
                serializeItems(order.getItems()),
                String.valueOf(order.getSubtotal()),
                String.valueOf(order.getDiscount()),
                String.valueOf(order.getDeliveryFee()),
                String.valueOf(order.getTotal()),
                order.getStatus().name(),
                order.getCreatedAt().toString(),
                String.valueOf(order.isDispatched())
        );
    }
}
