package com.example.grocery.controller;

import com.example.grocery.config.SessionUtil;
import com.example.grocery.service.CartService;
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
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(HttpSession session,
                           @RequestParam(defaultValue = "STANDARD") String orderType,
                           Model model) {
        String customerId = resolveCustomerId(session);
        String customerType = SessionUtil.isCustomer(session)
                ? SessionUtil.getCurrentCustomerType(session)
                : "GUEST";

        CartService.CartSummary summary = cartService.calculateSummary(customerId, customerType, orderType);
        model.addAttribute("summary", summary);
        model.addAttribute("orderType", orderType);
        model.addAttribute("isCustomer", SessionUtil.isCustomer(session));
        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable String productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        String customerId = resolveCustomerId(session);
        String cartType = SessionUtil.isCustomer(session) ? "REGISTERED" : "GUEST";

        try {
            cartService.addToCart(customerId, cartType, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products/catalog";
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable String productId,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String customerId = resolveCustomerId(session);
        try {
            cartService.updateQuantity(customerId, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cart quantity updated");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeItem(@PathVariable String productId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String customerId = resolveCustomerId(session);
        cartService.removeItem(customerId, productId);
        redirectAttributes.addFlashAttribute("success", "Item removed from cart");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        String customerId = resolveCustomerId(session);
        cartService.clearCart(customerId);
        redirectAttributes.addFlashAttribute("success", "Cart cleared");
        return "redirect:/cart";
    }

    private String resolveCustomerId(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) {
            return SessionUtil.getCurrentUserId(session);
        }
        return SessionUtil.getOrCreateGuestId(session);
    }
}
