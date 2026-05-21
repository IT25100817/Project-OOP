package com.example.grocery.repository;

import com.example.grocery.model.NonPerishableProduct;
import com.example.grocery.model.PerishableProduct;
import com.example.grocery.model.Product;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    public ProductRepository() {
        FileUtil.ensureBaseFiles();
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        for (String line : FileUtil.readAllLines(FileUtil.PRODUCTS_FILE)) {
            Product product = parse(line);
            if (product != null) {
                products.add(product);
            }
        }
        return products;
    }

    public Optional<Product> findById(String id) {
        return findAll().stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();
    }

    public Product save(Product product) {
        List<Product> products = findAll();
        if (product.getId() == null || product.getId().isBlank()) {
            List<String> ids = products.stream().map(Product::getId).collect(Collectors.toList());
            product.setId(FileUtil.nextId("PRD", ids));
        }
        products.add(product);
        writeAll(products);
        return product;
    }

    public boolean update(Product updatedProduct) {
        List<Product> products = findAll();
        boolean replaced = false;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equalsIgnoreCase(updatedProduct.getId())) {
                products.set(i, updatedProduct);
                replaced = true;
                break;
            }
        }
        if (replaced) {
            writeAll(products);
        }
        return replaced;
    }

    public boolean deleteById(String id) {
        List<Product> products = findAll();
        boolean removed = products.removeIf(p -> p.getId().equalsIgnoreCase(id));
        if (removed) {
            writeAll(products);
        }
        return removed;
    }

    private void writeAll(List<Product> products) {
        List<String> lines = products.stream().map(this::toLine).collect(Collectors.toList());
        FileUtil.writeAllLines(FileUtil.PRODUCTS_FILE, lines);
    }

    private Product parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) {
            return null;
        }

        String id = parts[0];
        String name = parts[1];
        String category = parts[2];
        String brand = parts[3];
        double price = FileUtil.parseDouble(parts[4], 0.0);
        int stock = FileUtil.parseInt(parts[5], 0);
        String type = parts[6];
        String expiryDate = parts[7];
        String status = parts[8];
        String imagePath = parts.length > 9 ? parts[9] : "";

        if ("PERISHABLE".equalsIgnoreCase(type)) {
            LocalDate expiry = null;
            if (!expiryDate.isBlank() && !"N/A".equalsIgnoreCase(expiryDate)) {
                try {
                    expiry = LocalDate.parse(expiryDate);
                } catch (Exception ignored) {
                }
            }
            return new PerishableProduct(id, name, category, brand, price, stock, status, imagePath, expiry);
        }

        return new NonPerishableProduct(id, name, category, brand, price, stock, status, imagePath);
    }

    private String toLine(Product product) {
        return String.join(FileUtil.DELIMITER,
                FileUtil.safe(product.getId()),
                FileUtil.safe(product.getName()),
                FileUtil.safe(product.getCategory()),
                FileUtil.safe(product.getBrand()),
                String.valueOf(product.getPrice()),
                String.valueOf(product.getStockQuantity()),
                product.getProductType(),
                FileUtil.safe(product.getExpiryDisplay()),
                FileUtil.safe(product.getStatus()),
                FileUtil.safe(product.getImagePath())
        );
    }
}
