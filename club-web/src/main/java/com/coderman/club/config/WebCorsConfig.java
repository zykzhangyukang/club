package com.coderman.club.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许跨域的源地址
        // 在Springboot2.4对应Spring5.3后在设置allowCredentials(true)的基础上不能直接使用通配符设置allowedOrigins，而是需要指定特定的URL。如果需要设置通配符，需要通过allowedOriginPatterns指定
        config.addAllowedOrigin("*");
        // 允许用户凭证跨域（cookie、HTTP认证及客户端SSL证明等）
        config.setAllowCredentials(true);
        // 允许跨域的头部
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 当前跨域请求最大有效时长，单位：秒
        config.setMaxAge(1800L);
        // 允许携带cookie
        config.setAllowCredentials(true);
        // 跨域路径配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
