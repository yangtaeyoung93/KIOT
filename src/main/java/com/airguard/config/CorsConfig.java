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
            "https://jejuairport.kweather.co.kr", "https://kohyoung.kweather.co.kr","http://issi.kweather.co.kr:8889")
        .allowedOrigins("*")
        .allowedMethods("*").allowCredentials(true);

    registry.addMapping("/api/platform/fota/r")
        .allowedOrigins("https://suncheontest.kweather.co.kr", "https://suncheon.kweather.co.kr")
        .allowedOrigins("*")
        .allowedMethods("*").allowCredentials(true);

    registry.addMapping("/api/collection/history")
            .allowedOrigins("http://localhost:8889",
                    "http://220.95.232.79:8889",
                    "http://issi.kweather.co.kr",
                    "http://issi.kweather.co.kr:8889",
                    "http://211.219.114.113:8889") //인천 서구 프로젝트의 데이터 수집용
            .allowedMethods("*")
            .allowCredentials(true);

    registry.addMapping("/system/member/device/ajax/fileDownload/**")
            .allowedOrigins("http://localhost")
            .allowedOrigins("*")
            .allowedMethods("*").allowCredentials(true);

    registry.addMapping("/api/air365/v3/data/detail")
            .allowedOrigins("http://localhost")
            .allowedOrigins("*")
            .allowedMethods("*").allowCredentials(true);
  }
}
