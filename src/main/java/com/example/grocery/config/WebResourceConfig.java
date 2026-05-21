package com.example.grocery.config;

import com.example.grocery.repository.FileUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = FileUtil.PRODUCT_IMAGES_DIR.toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(location);
    }
}
