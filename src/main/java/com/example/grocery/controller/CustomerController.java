package com.example.grocery.controller;

import com.example.grocery.config.SessionUtil;
import com.example.grocery.dto.CustomerForm;
import com.example.grocery.model.Customer;
import com.example.grocery.service.CustomerService;
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

import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(@RequestParam(required = false) String query,
                                HttpSession session,
                                Model model) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("customers", customerService.searchCustomers(query));
        return "customers";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isCustomer(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login as a customer");
            return "redirect:/login";
        }

        String customerId = SessionUtil.getCurrentUserId(session);
        Optional<Customer> customer = customerService.getById(customerId);
        if (customer.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Customer profile not found");
            return "redirect:/dashboard";
        }

        model.addAttribute("customerForm", toForm(customer.get(), false));
        model.addAttribute("profileMode", true);
        return "customer-form";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute CustomerForm customerForm,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isCustomer(session)) {
            return "redirect:/login";
        }

        try {
            customerForm.setId(SessionUtil.getCurrentUserId(session));
            customerService.updateCustomer(customerForm, false);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customers/profile";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable String id,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Customer> customer = customerService.getById(id);
        if (customer.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Customer not found");
            return "redirect:/customers";
        }

        model.addAttribute("customerForm", toForm(customer.get(), true));
        model.addAttribute("profileMode", false);
        return "customer-form";
    }

    @PostMapping("/admin/update")
    public String updateByAdmin(@ModelAttribute CustomerForm customerForm,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            customerService.updateCustomer(customerForm, true);
            redirectAttributes.addFlashAttribute("success", "Customer updated successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable String id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        if (customerService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Customer not found");
        }
        return "redirect:/customers";
    }

    private CustomerForm toForm(Customer customer, boolean includePassword) {
        CustomerForm form = new CustomerForm();
        form.setId(customer.getId());
        form.setUsername(customer.getUsername());
        form.setPassword(includePassword ? customer.getPassword() : "");
        form.setFullName(customer.getFullName());
        form.setEmail(customer.getEmail());
        form.setPhone(customer.getPhone());
        form.setAddress(customer.getAddress());
        form.setCustomerType(customer.getCustomerType());
        return form;
    }
}
