package com.example.grocery.config;

import com.example.grocery.repository.FileUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        FileUtil.ensureBaseFiles();
        seedAdmins();
        seedCustomers();
        seedProducts();
        seedIfEmpty(FileUtil.CART_FILE, List.of());
        seedIfEmpty(FileUtil.ORDERS_FILE, List.of());
        seedIfEmpty(FileUtil.DELIVERIES_FILE, List.of());
        seedIfEmpty(FileUtil.PAYMENTS_FILE, List.of());
    }

    private void seedAdmins() {
        if (FileUtil.readAllLines(FileUtil.ADMINS_FILE).isEmpty()) {
            FileUtil.writeAllLines(FileUtil.ADMINS_FILE, List.of(
                    "ADM001|admin|admin123|ADMIN"
            ));
        }
    }

    private void seedCustomers() {
        if (FileUtil.readAllLines(FileUtil.CUSTOMERS_FILE).isEmpty()) {
            FileUtil.writeAllLines(FileUtil.CUSTOMERS_FILE, List.of(
                    "CUS001|john|john123|John Silva|john@gmail.com|0711111111|Colombo|CUSTOMER|REGULAR|15",
                    "CUS002|maya|maya123|Maya Fernando|maya@gmail.com|0722222222|Kandy|CUSTOMER|PREMIUM|50"
            ));
        }
    }

    private void seedProducts() {
        if (FileUtil.readAllLines(FileUtil.PRODUCTS_FILE).isEmpty()) {
            FileUtil.writeAllLines(FileUtil.PRODUCTS_FILE, List.of(
                    "PRD001|Fresh Milk 1L|Dairy|Highland|3.50|40|PERISHABLE|2027-01-20|ACTIVE",
                    "PRD002|Basmati Rice 5kg|Grains|Nipuna|16.99|30|NON_PERISHABLE|N/A|ACTIVE",
                    "PRD003|Eggs Pack 12|Poultry|Farm Fresh|4.25|50|PERISHABLE|2026-12-30|ACTIVE",
                    "PRD004|Tea Powder 500g|Beverages|Dilmah|5.40|25|NON_PERISHABLE|N/A|ACTIVE"
            ));
        }
    }

    private void seedIfEmpty(java.nio.file.Path filePath, List<String> lines) {
        if (FileUtil.readAllLines(filePath).isEmpty()) {
            FileUtil.writeAllLines(filePath, lines);
        }
    }
}
