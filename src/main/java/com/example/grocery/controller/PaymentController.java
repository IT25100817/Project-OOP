package com.example.grocery.controller;

import com.example.grocery.config.SessionUtil;
import com.example.grocery.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public String listPayments(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (SessionUtil.isAdmin(session)) {
            model.addAttribute("payments", paymentService.getAllPayments());
        } else {
            model.addAttribute("payments", paymentService.getPaymentsByCustomer(SessionUtil.getCurrentUserId(session)));
        }

        return "payments";
    }

    @PostMapping("/update/{paymentId}")
    public String updatePaymentStatus(@PathVariable String paymentId,
                                      @RequestParam String status,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            paymentService.updateStatus(paymentId, status);
            redirectAttributes.addFlashAttribute("success", "Payment status updated");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/payments";
    }
}
