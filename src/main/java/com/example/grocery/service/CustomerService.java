package com.example.grocery.service;

import com.example.grocery.dto.CustomerForm;
import com.example.grocery.model.Customer;
import com.example.grocery.model.PremiumCustomer;
import com.example.grocery.model.RegularCustomer;
import com.example.grocery.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.isBlank()) {
            return getAllCustomers();
        }
        String normalized = query.toLowerCase();
        return getAllCustomers().stream()
                .filter(c -> c.getId().toLowerCase().contains(normalized)
                        || c.getUsername().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public Optional<Customer> getById(String id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer registerCustomer(CustomerForm form) {
        validateCustomerForm(form, true);
        if (customerRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Customer customer = buildCustomerFromForm(form, 0);
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(CustomerForm form, boolean allowPasswordChange) {
        validateCustomerForm(form, false);
        Customer existing = customerRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        String password = allowPasswordChange && form.getPassword() != null && !form.getPassword().isBlank()
                ? form.getPassword()
                : existing.getPassword();

        Customer updated = "PREMIUM".equalsIgnoreCase(form.getCustomerType())
                ? new PremiumCustomer(existing.getId(), existing.getUsername(), password,
                form.getFullName(), form.getEmail(), form.getPhone(), form.getAddress(), existing.getLoyaltyPoints())
                : new RegularCustomer(existing.getId(), existing.getUsername(), password,
                form.getFullName(), form.getEmail(), form.getPhone(), form.getAddress(), existing.getLoyaltyPoints());
        updated.setRole(existing.getRole());

        customerRepository.update(updated);
        return updated;
    }

    public void updateCustomerEntity(Customer customer) {
        customerRepository.update(customer);
    }

    public boolean deleteById(String id) {
        return customerRepository.deleteById(id);
    }

    private void validateCustomerForm(CustomerForm form, boolean includePasswordValidation) {
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (includePasswordValidation && (form.getPassword() == null || form.getPassword().length() < 6)) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (form.getFullName() == null || form.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (form.getEmail() == null || !EMAIL_PATTERN.matcher(form.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (form.getAddress() == null || form.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
    }

    private Customer buildCustomerFromForm(CustomerForm form, int loyaltyPoints) {
        if ("PREMIUM".equalsIgnoreCase(form.getCustomerType())) {
            return new PremiumCustomer(null, form.getUsername(), form.getPassword(), form.getFullName(),
                    form.getEmail(), form.getPhone(), form.getAddress(), loyaltyPoints);
        }
        return new RegularCustomer(null, form.getUsername(), form.getPassword(), form.getFullName(),
                form.getEmail(), form.getPhone(), form.getAddress(), loyaltyPoints);
    }
}
