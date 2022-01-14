package com.airguard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/client/**").allowedOrigins("*").allowCredentials(true)
        .allowedMethods("*");

    registry.addMapping("/api/custom/**")
        .allowedOrigins("https://suncheontest.kweather.co.kr", "https://suncheon.kweather.co.kr",
            "https://jejuairport.kweather.co.kr", "https://kohyoung.kweather.co.kr")
        .allowedOrigins("*")
        .allowedMethods("*").allowCredentials(true);

    registry.addMapping("/api/platform/fota/r")
        .allowedOrigins("https://suncheontest.kweather.co.kr", "https://suncheon.kweather.co.kr")
        .allowedOrigins("*")
        .allowedMethods("*").allowCredentials(true);
  }
}
