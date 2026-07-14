package com.parmy.parmy_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve purchase proof files
        registry.addResourceHandler("/purchases/**")
                .addResourceLocations("file:./purchases/");

        // Serve offer letter files
        registry.addResourceHandler("/offer_letters/**")
                .addResourceLocations("file:./offer_letters/");

        // Serve project abstract files (protected endpoint will be created separately)
        registry.addResourceHandler("/project_abstracts/**")
                .addResourceLocations("file:./project_abstracts/");

        // Serve gallery upload files
        registry.addResourceHandler("/gallery_uploads/**")
                .addResourceLocations("file:./gallery_uploads/");

        // Serve project thumbnail files
        registry.addResourceHandler("/project_thumbnails/**")
                .addResourceLocations("file:./project_thumbnails/");
    }
}
