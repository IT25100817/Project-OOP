package com.example.grocery.controller;

import com.example.grocery.config.PaginationHelper;
import com.example.grocery.config.SessionUtil;
import com.example.grocery.dto.OrderForm;
import com.example.grocery.model.Order;
import com.example.grocery.service.CartService;
import com.example.grocery.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session,
                           @RequestParam(defaultValue = "STANDARD") String orderType,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isCustomer(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login as a customer to checkout");
            return "redirect:/login";
        }

        String customerId = SessionUtil.getCurrentUserId(session);
        String customerType = SessionUtil.getCurrentCustomerType(session);
        CartService.CartSummary summary = cartService.calculateSummary(customerId, customerType, orderType);
        if (summary.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cannot checkout with an empty cart");
            return "redirect:/cart";
        }

        OrderForm orderForm = new OrderForm();
        orderForm.setOrderType(orderType);
        orderForm.setPaymentType("COD");

        model.addAttribute("summary", summary);
        model.addAttribute("orderForm", orderForm);
        return "checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@ModelAttribute OrderForm orderForm,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isCustomer(session)) {
            return "redirect:/login";
        }

        try {
            String customerId = SessionUtil.getCurrentUserId(session);
            String customerType = SessionUtil.getCurrentCustomerType(session);
            Order order = orderService.placeOrder(customerId, customerType,
                    orderForm.getOrderType(), orderForm.getPaymentType(), orderForm.getPaymentReference());
            redirectAttributes.addFlashAttribute("success", "Order placed successfully: " + order.getId());
            return "redirect:/orders/" + order.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    @GetMapping
    public String listOrders(HttpSession session,
                             @RequestParam(required = false) String status,
                             @RequestParam(defaultValue = "dateDesc") String sortBy,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "8") int size,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login first");
            return "redirect:/login";
        }

        List<Order> orders = SessionUtil.isAdmin(session)
                ? orderService.getAllOrders()
                : orderService.getOrdersForCustomer(SessionUtil.getCurrentUserId(session));

        List<Order> filteredSortedOrders = sortOrders(filterOrdersByStatus(orders, status), sortBy);
        PaginationHelper.PageSlice<Order> pageSlice = PaginationHelper.paginate(filteredSortedOrders, page, size);

        model.addAttribute("orders", pageSlice.items());
        model.addAttribute("status", status == null ? "" : status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", pageSlice.size());
        model.addAttribute("currentPage", pageSlice.currentPage());
        model.addAttribute("totalPages", pageSlice.totalPages());
        model.addAttribute("totalItems", pageSlice.totalItems());
        return "orders";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@PathVariable String orderId,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        Optional<Order> orderOptional = orderService.getById(orderId);
        if (orderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid order ID");
            return "redirect:/orders";
        }

        Order order = orderOptional.get();
        if (!SessionUtil.isAdmin(session) && !order.getCustomerId().equals(SessionUtil.getCurrentUserId(session))) {
            redirectAttributes.addFlashAttribute("error", "Access denied");
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "order-details";
    }

    @GetMapping("/track")
    public String trackOrder(@RequestParam String orderId,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        Optional<Order> orderOptional = orderService.getById(orderId);
        if (orderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Order not found");
            return "redirect:/orders";
        }

        model.addAttribute("order", orderOptional.get());
        return "order-details";
    }

    @PostMapping("/status/{orderId}")
    public String updateStatus(@PathVariable String orderId,
                               @RequestParam String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable String orderId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isCustomer(session)) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId, SessionUtil.getCurrentUserId(session));
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders";
    }

    private List<Order> filterOrdersByStatus(List<Order> orders, String status) {
        if (status == null || status.isBlank()) {
            return orders;
        }
        return orders.stream()
                .filter(order -> order.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }

    private List<Order> sortOrders(List<Order> orders, String sortBy) {
        Comparator<Order> comparator = switch (sortBy) {
            case "dateAsc" -> Comparator.comparing(Order::getCreatedAt);
            case "totalLow" -> Comparator.comparingDouble(Order::getTotal);
            case "totalHigh" -> Comparator.comparingDouble(Order::getTotal).reversed();
            case "status" -> Comparator.comparing(order -> order.getStatus().name());
            default -> Comparator.comparing(Order::getCreatedAt).reversed();
        };

        return orders.stream().sorted(comparator).toList();
    }
}
