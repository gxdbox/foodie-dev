package com.imooc;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    public CorsConfig() {
    }


    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://shop.pinxow.com:8080");
        config.addAllowedOrigin("http://center.pinxow.com:8080");
        config.addAllowedOrigin("http://shop2.pinxow.com:8080");
        config.addAllowedOrigin("http://center2.pinxow.com:8080");
        config.addAllowedOrigin("http://shop3.pinxow.com:8080");
        config.addAllowedOrigin("http://center3.pinxow.com:8080");
        config.addAllowedOrigin("http://shop.pinxow.com");
        config.addAllowedOrigin("http://center.pinxow.com");
        config.addAllowedOrigin("http://shop2.pinxow.com");
        config.addAllowedOrigin("http://center2.pinxow.com");
        config.addAllowedOrigin("http://shop3.pinxow.com");
        config.addAllowedOrigin("http://center3.pinxow.com");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
