package com.example.grocery.controller;

import com.example.grocery.dto.CustomerForm;
import com.example.grocery.service.AuthService;
import com.example.grocery.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final AuthService authService;
    private final CustomerService customerService;

    public AuthController(AuthService authService, CustomerService customerService) {
        this.authService = authService;
        this.customerService = customerService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        return authService.authenticate(username, password)
                .map(user -> {
                    session.setAttribute("userId", user.id());
                    session.setAttribute("username", user.username());
                    session.setAttribute("role", user.role());
                    session.setAttribute("customerType", user.customerType());
                    if ("ADMIN".equals(user.role())) {
                        return "redirect:/admin/dashboard";
                    }
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                    return "redirect:/login";
                });
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        CustomerForm form = new CustomerForm();
        form.setCustomerType("REGULAR");
        model.addAttribute("customerForm", form);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CustomerForm customerForm,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            customerService.registerCustomer(customerForm);
            redirectAttributes.addFlashAttribute("success", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("customerForm", customerForm);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been logged out");
        return "redirect:/";
    }
}
