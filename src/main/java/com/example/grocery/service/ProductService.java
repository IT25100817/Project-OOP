package com.example.grocery.service;

import com.example.grocery.dto.ProductForm;
import com.example.grocery.model.NonPerishableProduct;
import com.example.grocery.model.PerishableProduct;
import com.example.grocery.model.Product;
import com.example.grocery.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            return getAllProducts();
        }
        String normalized = query.toLowerCase();
        return getAllProducts().stream()
                .filter(product -> product.getName().toLowerCase().contains(normalized)
                        || product.getCategory().toLowerCase().contains(normalized)
                        || product.getBrand().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductForm form) {
        validateProductForm(form);
        Product product = buildProductFromForm(form, null, form.getImagePath());
        return productRepository.save(product);
    }

    public Product updateProduct(ProductForm form) {
        validateProductForm(form);
        Product existing = productRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Product updated = buildProductFromForm(form, existing.getId(), form.getImagePath());
        productRepository.update(updated);
        return updated;
    }

    public boolean deleteById(String id) {
        return productRepository.deleteById(id);
    }

    public boolean reduceStock(String productId, int qty) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || qty <= 0 || !product.hasSufficientStock(qty)) {
            return false;
        }
        product.reduceStock(qty);
        return productRepository.update(product);
    }

    public void increaseStock(String productId, int qty) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || qty <= 0) {
            return;
        }
        product.setStockQuantity(product.getStockQuantity() + qty);
        productRepository.update(product);
    }

    private void validateProductForm(ProductForm form) {
        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (form.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (form.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (form.getProductType() == null || form.getProductType().isBlank()) {
            throw new IllegalArgumentException("Product type is required");
        }
        if ("PERISHABLE".equalsIgnoreCase(form.getProductType())
                && (form.getExpiryDate() == null || form.getExpiryDate().isBlank())) {
            throw new IllegalArgumentException("Expiry date is required for perishable products");
        }
    }

    private Product buildProductFromForm(ProductForm form, String productId, String imagePath) {
        if ("PERISHABLE".equalsIgnoreCase(form.getProductType())) {
            LocalDate expiryDate;
            try {
                expiryDate = LocalDate.parse(form.getExpiryDate());
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid expiry date format");
            }
            return new PerishableProduct(productId, form.getName(), form.getCategory(), form.getBrand(),
                    form.getPrice(), form.getStockQuantity(), form.getStatus(), imagePath, expiryDate);
        }
        return new NonPerishableProduct(productId, form.getName(), form.getCategory(), form.getBrand(),
                form.getPrice(), form.getStockQuantity(), form.getStatus(), imagePath);
    }
}
