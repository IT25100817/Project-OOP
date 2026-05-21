package com.example.grocery.service;

import com.example.grocery.model.Cart;
import com.example.grocery.model.CartItem;
import com.example.grocery.model.ExpressDelivery;
import com.example.grocery.model.GuestCart;
import com.example.grocery.model.Product;
import com.example.grocery.model.RegisteredCart;
import com.example.grocery.model.StandardDelivery;
import com.example.grocery.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    public CartService(CartRepository cartRepository, ProductService productService, CustomerService customerService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.customerService = customerService;
    }

    public List<CartItem> getCartItems(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    public void addToCart(String customerId, String cartType, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Product product = productService.getById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.hasSufficientStock(quantity)) {
            throw new IllegalArgumentException("Insufficient stock for selected product");
        }

        List<CartItem> items = new ArrayList<>(cartRepository.findByCustomerId(customerId));
        Optional<CartItem> existing = items.stream()
                .filter(item -> item.getProductId().equalsIgnoreCase(productId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + quantity;
            if (!product.hasSufficientStock(newQty)) {
                throw new IllegalArgumentException("Cannot exceed available stock");
            }
            item.setQuantity(newQty);
        } else {
            CartItem item = new CartItem(
                    cartRepository.generateCartId(),
                    customerId,
                    cartType,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity
            );
            items.add(item);
        }

        cartRepository.replaceCustomerCart(customerId, items);
    }

    public void updateQuantity(String customerId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        Product product = productService.getById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!product.hasSufficientStock(quantity)) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        List<CartItem> items = new ArrayList<>(cartRepository.findByCustomerId(customerId));
        for (CartItem item : items) {
            if (item.getProductId().equalsIgnoreCase(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        cartRepository.replaceCustomerCart(customerId, items);
    }

    public void removeItem(String customerId, String productId) {
        List<CartItem> items = new ArrayList<>(cartRepository.findByCustomerId(customerId));
        items.removeIf(item -> item.getProductId().equalsIgnoreCase(productId));
        cartRepository.replaceCustomerCart(customerId, items);
    }

    public void clearCart(String customerId) {
        cartRepository.clearCart(customerId);
    }

    public CartSummary calculateSummary(String customerId, String customerType, String orderType) {
        List<CartItem> items = cartRepository.findByCustomerId(customerId);
        Cart cart = buildCart(customerId, customerType, items);

        double subtotal = cart.calculateSubtotal();
        double discount = cart.applyDiscount(subtotal);
        double deliveryFee = resolveDeliveryFee(orderType, subtotal);
        double total = Math.max(0.0, subtotal - discount + deliveryFee);

        return new CartSummary(items, subtotal, discount, deliveryFee, total);
    }

    private Cart buildCart(String customerId, String customerType, List<CartItem> items) {
        if (customerId.startsWith("GUEST")) {
            return new GuestCart(customerId, items);
        }

        double discountRate = 0.0;
        if ("PREMIUM".equalsIgnoreCase(customerType)) {
            discountRate = 0.10;
        }

        // OOP: Inheritance - RegisteredCart uses Cart abstraction
        return new RegisteredCart(customerId, items, discountRate);
    }

    private double resolveDeliveryFee(String orderType, double subtotal) {
        if ("EXPRESS".equalsIgnoreCase(orderType)) {
            return new ExpressDelivery().calculateDeliveryFee(subtotal);
        }
        return new StandardDelivery().calculateDeliveryFee(subtotal);
    }

    public static class CartSummary {
        private final List<CartItem> items;
        private final double subtotal;
        private final double discount;
        private final double deliveryFee;
        private final double total;

        public CartSummary(List<CartItem> items, double subtotal, double discount, double deliveryFee, double total) {
            this.items = items;
            this.subtotal = subtotal;
            this.discount = discount;
            this.deliveryFee = deliveryFee;
            this.total = total;
        }

        public List<CartItem> getItems() {
            return items;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public double getDiscount() {
            return discount;
        }

        public double getDeliveryFee() {
            return deliveryFee;
        }

        public double getTotal() {
            return total;
        }
    }
}
