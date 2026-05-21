package com.example.grocery.repository;

import com.example.grocery.model.Customer;
import com.example.grocery.model.PremiumCustomer;
import com.example.grocery.model.RegularCustomer;
import com.example.grocery.model.UserRole;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CustomerRepository {

    public CustomerRepository() {
        FileUtil.ensureBaseFiles();
    }

    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        for (String line : FileUtil.readAllLines(FileUtil.CUSTOMERS_FILE)) {
            Customer customer = parse(line);
            if (customer != null) {
                customers.add(customer);
            }
        }
        return customers;
    }

    public Optional<Customer> findById(String id) {
        return findAll().stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<Customer> findByUsername(String username) {
        return findAll().stream().filter(c -> c.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public Customer save(Customer customer) {
        List<Customer> customers = findAll();
        if (customer.getId() == null || customer.getId().isBlank()) {
            List<String> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
            customer.setId(FileUtil.nextId("CUS", ids));
        }
        customers.add(customer);
        writeAll(customers);
        return customer;
    }

    public boolean update(Customer updatedCustomer) {
        List<Customer> customers = findAll();
        boolean replaced = false;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId().equalsIgnoreCase(updatedCustomer.getId())) {
                customers.set(i, updatedCustomer);
                replaced = true;
                break;
            }
        }
        if (replaced) {
            writeAll(customers);
        }
        return replaced;
    }

    public boolean deleteById(String id) {
        List<Customer> customers = findAll();
        boolean removed = customers.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (removed) {
            writeAll(customers);
        }
        return removed;
    }

    private void writeAll(List<Customer> customers) {
        List<String> lines = customers.stream().map(this::toLine).collect(Collectors.toList());
        FileUtil.writeAllLines(FileUtil.CUSTOMERS_FILE, lines);
    }

    private Customer parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 10) {
            return null;
        }

        String id = parts[0];
        String username = parts[1];
        String password = parts[2];
        String fullName = parts[3];
        String email = parts[4];
        String phone = parts[5];
        String address = parts[6];
        UserRole role = parseRole(parts[7]);
        String type = parts[8];
        int loyalty = FileUtil.parseInt(parts[9], 0);

        Customer customer;
        if ("PREMIUM".equalsIgnoreCase(type)) {
            customer = new PremiumCustomer(id, username, password, fullName, email, phone, address, loyalty);
        } else {
            customer = new RegularCustomer(id, username, password, fullName, email, phone, address, loyalty);
        }
        customer.setRole(role);
        return customer;
    }

    private UserRole parseRole(String roleText) {
        try {
            return UserRole.valueOf(roleText);
        } catch (Exception ex) {
            return UserRole.CUSTOMER;
        }
    }

    private String toLine(Customer customer) {
        return String.join(FileUtil.DELIMITER,
                FileUtil.safe(customer.getId()),
                FileUtil.safe(customer.getUsername()),
                FileUtil.safe(customer.getPassword()),
                FileUtil.safe(customer.getFullName()),
                FileUtil.safe(customer.getEmail()),
                FileUtil.safe(customer.getPhone()),
                FileUtil.safe(customer.getAddress()),
                customer.getRole().name(),
                customer.getCustomerType(),
                String.valueOf(customer.getLoyaltyPoints())
        );
    }
}
