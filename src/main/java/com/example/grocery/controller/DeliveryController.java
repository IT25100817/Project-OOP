package com.example.grocery.controller;

import com.example.grocery.config.SessionUtil;
import com.example.grocery.service.DeliveryService;
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
@RequestMapping("/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public String listDeliveries(HttpSession session, Model model) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("deliveries", deliveryService.getAllDeliveries());
        return "deliveries";
    }

    @PostMapping("/update/{deliveryId}")
    public String updateDelivery(@PathVariable String deliveryId,
                                 @RequestParam String assignedPerson,
                                 @RequestParam String status,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            deliveryService.updateDelivery(deliveryId, assignedPerson, status);
            redirectAttributes.addFlashAttribute("success", "Delivery updated successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/deliveries";
    }
}
