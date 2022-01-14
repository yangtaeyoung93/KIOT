package com.airguard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.airguard.util.HandlerAuthInterceptor;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

  @Bean
  public HandlerAuthInterceptor handshakeInterceptor() {
    return new HandlerAuthInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(handshakeInterceptor())
        // 에외 Control 정의
        .excludePathPatterns("/") // 로그인 페이지 .
        .excludePathPatterns("/adminLogin") // 로그인 페이지 (관리자) .
        .excludePathPatterns("/resources/**").excludePathPatterns("/static/**")
        .excludePathPatterns("/error").excludePathPatterns("/er/404").excludePathPatterns("/js/**")
        .excludePathPatterns("/images/**").excludePathPatterns("/css/**")
        .excludePathPatterns("/font/**").excludePathPatterns("/fonts/**")
        .excludePathPatterns("/lib/**").excludePathPatterns("/doLogin")
        .excludePathPatterns("/doLogin2").excludePathPatterns("/login")
        .excludePathPatterns("/loginSuccess").excludePathPatterns("/collection/popup")
        .excludePathPatterns("/system/push/message/**") // 파일 미리보기, 다운로드
        .excludePathPatterns("/system/member/device/ajax/fileDownload/**") // 파일 미리보기, 다운로드
        .excludePathPatterns("/system/member/device/fileAllUpload")
        // API .
        .excludePathPatterns("/api/login")
        .excludePathPatterns("/api/client/**").excludePathPatterns("/api/collection/**")
        .excludePathPatterns("/api/custom/**").excludePathPatterns("/api/dong/dongList/dev").excludePathPatterns("/api/platform/fota/r")
        .excludePathPatterns("/api/dong/**") // 내부 이용
        .excludePathPatterns("/api/dashboard/receive/cnt") // 내부 이용 API .
        .excludePathPatterns("/api/app/**") // APP 연계 API .
        .excludePathPatterns("/api/air365/**") // AIR365 API .
        .excludePathPatterns("/api/platform/**") // APP 연계 API .
        .excludePathPatterns("/api/data") // 삼성 b.IoT 연계 API .
        .excludePathPatterns("/api/datacenter/**") // 개인, 그룹 데이터 센터 이용 API .
        .excludePathPatterns("/datacenter/**"); // 개인, 그룹 데이터 센터 페이지 .
  }
}
