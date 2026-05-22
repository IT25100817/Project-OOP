package com.example.grocery.controller;

import com.example.grocery.config.SessionUtil;
import com.example.grocery.model.Delivery;
import com.example.grocery.model.Order;
import com.example.grocery.model.Payment;
import com.example.grocery.service.CustomerService;
import com.example.grocery.service.DeliveryService;
import com.example.grocery.service.OrderService;
import com.example.grocery.service.PaymentService;
import com.example.grocery.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller

@RequestMapping("/admin")
public class AdminController {
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;


    public AdminController(CustomerService customerService, ProductService productService,
                           OrderService orderService, DeliveryService deliveryService,
                           PaymentService paymentService) {
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getAllOrders();
        List<Payment> payments = paymentService.getAllPayments();
        List<Delivery> deliveries = deliveryService.getAllDeliveries();

        model.addAttribute("customerCount", customerService.getAllCustomers().size());
        model.addAttribute("productCount", productService.getAllProducts().size());
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("deliveryCount", deliveries.size());
        model.addAttribute("paymentCount", payments.size());

        List<String> orderStatusLabels = List.of("PENDING", "CONFIRMED", "DELIVERED", "CANCELLED");
        List<Long> orderStatusCounts = orderStatusLabels.stream()
                .map(label -> orders.stream().filter(order -> order.getStatus().name().equals(label)).count())
                .toList();

        List<String> paymentStatusLabels = List.of("PENDING", "CONFIRMED", "REFUNDED", "CANCELLED");
        List<Long> paymentStatusCounts = paymentStatusLabels.stream()
                .map(label -> payments.stream().filter(payment -> payment.getStatus().name().equals(label)).count())
                .toList();

        List<String> deliveryStatusLabels = List.of("SCHEDULED", "ASSIGNED", "DISPATCHED", "DELIVERED", "CANCELLED");
        List<Long> deliveryStatusCounts = deliveryStatusLabels.stream()
                .map(label -> deliveries.stream().filter(delivery -> delivery.getStatus().name().equals(label)).count())
                .toList();

        List<String> trendLabels = new ArrayList<>();
        List<Long> trendCounts = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            long count = orders.stream().filter(order -> order.getCreatedAt().toLocalDate().equals(day)).count();
            trendLabels.add(day.format(dateFormatter));
            trendCounts.add(count);
        }

        model.addAttribute("orderStatusLabels", orderStatusLabels);
        model.addAttribute("orderStatusCounts", orderStatusCounts);
        model.addAttribute("paymentStatusLabels", paymentStatusLabels);
        model.addAttribute("paymentStatusCounts", paymentStatusCounts);
        model.addAttribute("deliveryStatusLabels", deliveryStatusLabels);
        model.addAttribute("deliveryStatusCounts", deliveryStatusCounts);
        model.addAttribute("trendLabels", trendLabels);
        model.addAttribute("trendCounts", trendCounts);

        return "admin-dashboard";
    }
}
