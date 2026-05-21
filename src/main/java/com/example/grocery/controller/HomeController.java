package com.example.grocery.controller;

import com.example.grocery.config.SessionUtil;
import com.example.grocery.model.Order;
import com.example.grocery.model.Product;
import com.example.grocery.service.OrderService;
import com.example.grocery.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final ProductService productService;
    private final OrderService orderService;

    public HomeController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Product> featured = productService.getAllProducts().stream().limit(4).toList();
        model.addAttribute("featuredProducts", featured);
        model.addAttribute("isLoggedIn", SessionUtil.isLoggedIn(session));
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!SessionUtil.isCustomer(session)) {
            return "redirect:/login";
        }

        String customerId = SessionUtil.getCurrentUserId(session);
        List<Order> customerOrders = orderService.getOrdersForCustomer(customerId);

        long pendingOrders = customerOrders.stream().filter(order -> order.getStatus().name().equals("PENDING")).count();
        long confirmedOrders = customerOrders.stream().filter(order -> order.getStatus().name().equals("CONFIRMED")).count();
        long deliveredOrders = customerOrders.stream().filter(order -> order.getStatus().name().equals("DELIVERED")).count();
        long cancelledOrders = customerOrders.stream().filter(order -> order.getStatus().name().equals("CANCELLED")).count();

        List<String> statusLabels = List.of("PENDING", "CONFIRMED", "DELIVERED", "CANCELLED");
        List<Long> statusValues = List.of(pendingOrders, confirmedOrders, deliveredOrders, cancelledOrders);

        List<Order> latestOrders = customerOrders.stream().limit(6).toList();
        List<String> recentOrderLabels = latestOrders.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(Order::getId)
                .toList();
        List<Double> recentOrderTotals = latestOrders.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(Order::getTotal)
                .toList();

        model.addAttribute("orderCount", customerOrders.size());
        model.addAttribute("productCount", productService.getAllProducts().size());
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("customerOrderStatusLabels", statusLabels);
        model.addAttribute("customerOrderStatusValues", statusValues);
        model.addAttribute("recentOrderLabels", recentOrderLabels);
        model.addAttribute("recentOrderTotals", recentOrderTotals);
        return "dashboard";
    }
}
