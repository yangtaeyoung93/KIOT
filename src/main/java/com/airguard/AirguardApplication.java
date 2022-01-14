package com.airguard;

import javax.servlet.http.HttpSessionListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.airguard.util.SessionListener;

@SpringBootApplication
public class AirguardApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(AirguardApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(AirguardApplication.class);
  }

  @Bean
  public HttpSessionListener httpSessionListener() {
    return new SessionListener();
  }
}
