package com.example.grocery.service;

import com.example.grocery.model.Customer;
import com.example.grocery.model.ExpressDelivery;
import com.example.grocery.model.Order;
import com.example.grocery.model.OrderItem;
import com.example.grocery.model.OrderStatus;
import com.example.grocery.model.Product;
import com.example.grocery.model.StandardDelivery;
import com.example.grocery.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, CartService cartService,
                        ProductService productService, CustomerService customerService,
                        DeliveryService deliveryService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.customerService = customerService;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;
    }

    public Order placeOrder(String customerId, String customerType, String orderType,
                            String paymentType, String paymentReference) {
        List<com.example.grocery.model.CartItem> cartItems = cartService.getCartItems(customerId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order because cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double subtotal = 0;

        for (com.example.grocery.model.CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + cartItem.getProductId()));

            if (!product.isValidForSale()) {
                throw new IllegalArgumentException("Product is not valid for sale: " + product.getName());
            }

            if (!product.hasSufficientStock(cartItem.getQuantity())) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            orderItems.add(new OrderItem(product.getId(), product.getName(), product.getPrice(), cartItem.getQuantity()));
            subtotal += product.getPrice() * cartItem.getQuantity();
        }

        Optional<Customer> customerOptional = customerService.getById(customerId);
        double subtotalValue = subtotal;
        double discount = customerOptional.map(customer -> customer.calculateDiscount(subtotalValue)).orElse(0.0);
        double deliveryFee = resolveDeliveryFee(orderType, subtotal);
        double total = Math.max(0.0, subtotal - discount + deliveryFee);

        String customerName = customerOptional.map(Customer::getFullName).orElse("Guest Customer");

        Order order = new Order(null, customerId, customerName, orderType, orderItems,
                subtotal, discount, deliveryFee, total, OrderStatus.PENDING, LocalDateTime.now(), false);

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            boolean reduced = productService.reduceStock(item.getProductId(), item.getQuantity());
            if (!reduced) {
                throw new IllegalStateException("Failed to reduce stock for product: " + item.getProductId());
            }
        }

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + customer.calculateLoyaltyReward(total));
            customerService.updateCustomerEntity(customer);
        }

        cartService.clearCart(customerId);
        paymentService.createPaymentForOrder(savedOrder, paymentType, paymentReference);

        return savedOrder;
    }

    public List<Order> getOrdersForCustomer(String customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        orders.sort(Comparator.comparing(Order::getCreatedAt).reversed());
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.sort(Comparator.comparing(Order::getCreatedAt).reversed());
        return orders;
    }

    public Optional<Order> getById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public boolean updateOrderStatus(String orderId, String statusText) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus status = parseStatus(statusText);
        order.setStatus(status);
        if (status == OrderStatus.DELIVERED) {
            order.setDispatched(true);
        }

        boolean updated = orderRepository.update(order);
        if (updated && status == OrderStatus.CONFIRMED) {
            deliveryService.createDeliveryForOrder(order);
        }

        return updated;
    }

    public boolean cancelOrder(String orderId, String customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomerId().equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("You can only cancel your own orders");
        }

        if (!order.canCancel()) {
            throw new IllegalArgumentException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.update(order);
    }

    // Returns a simple one-line summary of the order
    // This is a basic example of Abstraction: the caller gets a summary without
    // knowing how the text is put together
    public String getOrderSummary(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return "Order not found";
        }
        Order order = orderOptional.get();
        return "Order " + order.getId()
                + " | Customer: " + order.getCustomerName()
                + " | Status: " + order.getStatus().name()
                + " | Total: Rs." + order.getTotal();
    }

    private OrderStatus parseStatus(String statusText) {
        try {
            return OrderStatus.valueOf(statusText.toUpperCase());
        } catch (Exception ex) {
            return OrderStatus.PENDING;
        }
    }

    // Polymorphism: resolveDeliveryFee calls calculateDeliveryFee() on different
    // objects (ExpressDelivery or StandardDelivery). Same method name, different behaviour.
    private double resolveDeliveryFee(String orderType, double subtotal) {
        if ("EXPRESS".equalsIgnoreCase(orderType)) {
            return new ExpressDelivery().calculateDeliveryFee(subtotal);
        }
        return new StandardDelivery().calculateDeliveryFee(subtotal);
    }
}
