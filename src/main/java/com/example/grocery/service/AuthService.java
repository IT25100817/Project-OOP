package com.example.grocery.service;

import com.example.grocery.model.Customer;
import com.example.grocery.model.UserRole;
import com.example.grocery.repository.CustomerRepository;
import com.example.grocery.repository.FileUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final CustomerRepository customerRepository;

    public AuthService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        ensureDefaultAdmin();
    }

    public Optional<SessionUser> authenticate(String username, String password) {
        Optional<SessionUser> admin = authenticateAdmin(username, password);
        if (admin.isPresent()) {
            return admin;
        }

        Optional<Customer> customer = customerRepository.findByUsername(username)
                .filter(c -> c.getPassword().equals(password));

        if (customer.isPresent()) {
            Customer c = customer.get();
            return Optional.of(new SessionUser(c.getId(), c.getUsername(), c.getRole().name(), c.getCustomerType()));
        }

        return Optional.empty();
    }

    private Optional<SessionUser> authenticateAdmin(String username, String password) {
        for (String line : FileUtil.readAllLines(FileUtil.ADMINS_FILE)) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 4) {
                continue;
            }
            String id = parts[0];
            String fileUsername = parts[1];
            String filePassword = parts[2];
            String role = parts[3];
            if (fileUsername.equals(username) && filePassword.equals(password) && "ADMIN".equalsIgnoreCase(role)) {
                return Optional.of(new SessionUser(id, fileUsername, UserRole.ADMIN.name(), "ADMIN"));
            }
        }
        return Optional.empty();
    }

    private void ensureDefaultAdmin() {
        List<String> admins = FileUtil.readAllLines(FileUtil.ADMINS_FILE);
        boolean hasAdmin = admins.stream().anyMatch(line -> line.contains("|admin|"));
        if (!hasAdmin) {
            FileUtil.appendLine(FileUtil.ADMINS_FILE, "ADM001|admin|admin123|ADMIN");
        }
    }

    public record SessionUser(String id, String username, String role, String customerType) {
    }
}
