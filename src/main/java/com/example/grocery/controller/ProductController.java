package com.example.grocery.controller;

import com.example.grocery.config.PaginationHelper;
import com.example.grocery.config.SessionUtil;
import com.example.grocery.dto.ProductForm;
import com.example.grocery.model.NonPerishableProduct;
import com.example.grocery.model.PerishableProduct;
import com.example.grocery.model.Product;
import com.example.grocery.repository.FileUtil;
import com.example.grocery.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String query,
                          @RequestParam(defaultValue = "nameAsc") String sortBy,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "9") int size,
                          Model model) {
        List<Product> sortedProducts = sortProducts(productService.searchProducts(query), sortBy);
        PaginationHelper.PageSlice<Product> pageSlice = PaginationHelper.paginate(sortedProducts, page, size);

        model.addAttribute("products", pageSlice.items());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", pageSlice.size());
        model.addAttribute("currentPage", pageSlice.currentPage());
        model.addAttribute("totalPages", pageSlice.totalPages());
        model.addAttribute("totalItems", pageSlice.totalItems());
        return "product-catalog";
    }

    @GetMapping
    public String listForAdmin(@RequestParam(required = false) String query,
                               @RequestParam(defaultValue = "nameAsc") String sortBy,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               HttpSession session,
                               Model model) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        List<Product> sortedProducts = sortProducts(productService.searchProducts(query), sortBy);
        PaginationHelper.PageSlice<Product> pageSlice = PaginationHelper.paginate(sortedProducts, page, size);

        model.addAttribute("products", pageSlice.items());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", pageSlice.size());
        model.addAttribute("currentPage", pageSlice.currentPage());
        model.addAttribute("totalPages", pageSlice.totalPages());
        model.addAttribute("totalItems", pageSlice.totalItems());
        return "products";
    }

    @GetMapping("/new")
    public String newProduct(HttpSession session, Model model) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        ProductForm form = new ProductForm();
        form.setStatus("ACTIVE");
        form.setProductType("NON_PERISHABLE");
        model.addAttribute("productForm", form);
        return "product-form";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable String id,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Product> product = productService.getById(id);
        if (product.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/products";
        }

        model.addAttribute("productForm", toForm(product.get()));
        return "product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute ProductForm productForm,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = storeProductImage(imageFile);
                productForm.setImagePath(imagePath);
            }

            if (productForm.getId() == null || productForm.getId().isBlank()) {
                productService.createProduct(productForm);
                redirectAttributes.addFlashAttribute("success", "Product added successfully");
            } else {
                productService.updateProduct(productForm);
                redirectAttributes.addFlashAttribute("success", "Product updated successfully");
            }
            return "redirect:/products";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("productForm", productForm);
            return "product-form";
        } catch (Exception ex) {
            model.addAttribute("error", "Image upload failed. Please use JPG, PNG, WEBP, or GIF.");
            model.addAttribute("productForm", productForm);
            return "product-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isAdmin(session)) {
            return "redirect:/login";
        }

        if (productService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found");
        }
        return "redirect:/products";
    }

    private ProductForm toForm(Product product) {
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setCategory(product.getCategory());
        form.setBrand(product.getBrand());
        form.setPrice(product.getPrice());
        form.setStockQuantity(product.getStockQuantity());
        form.setProductType(product.getProductType());
        form.setStatus(product.getStatus());
        form.setImagePath(product.getImagePath());

        if (product instanceof PerishableProduct perishableProduct && perishableProduct.getExpiryDate() != null) {
            form.setExpiryDate(perishableProduct.getExpiryDate().toString());
        } else if (product instanceof NonPerishableProduct) {
            form.setExpiryDate("");
        }

        return form;
    }

    private String storeProductImage(MultipartFile imageFile) throws Exception {
        String originalName = imageFile.getOriginalFilename() == null ? "" : imageFile.getOriginalFilename().toLowerCase();
        String extension = getExtension(originalName);
        if (!List.of("jpg", "jpeg", "png", "webp", "gif").contains(extension)) {
            throw new IllegalArgumentException("Unsupported image type");
        }

        Files.createDirectories(FileUtil.PRODUCT_IMAGES_DIR);
        String fileName = UUID.randomUUID() + "." + extension;
        Path target = FileUtil.PRODUCT_IMAGES_DIR.resolve(fileName);
        Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/products/" + fileName;
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    private List<Product> sortProducts(List<Product> products, String sortBy) {
        Comparator<Product> comparator = switch (sortBy) {
            case "nameDesc" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed();
            case "priceLow" -> Comparator.comparingDouble(Product::getPrice);
            case "priceHigh" -> Comparator.comparingDouble(Product::getPrice).reversed();
            case "stockLow" -> Comparator.comparingInt(Product::getStockQuantity);
            case "stockHigh" -> Comparator.comparingInt(Product::getStockQuantity).reversed();
            default -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        };

        return products.stream().sorted(comparator).toList();
    }
}
