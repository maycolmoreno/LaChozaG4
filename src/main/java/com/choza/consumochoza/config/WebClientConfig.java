package com.choza.consumochoza.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpSession;

@Configuration
public class WebClientConfig {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .filter((request, next) -> {
                    // Inyectar JWT desde la sesión HTTP en cada petición
                    ServletRequestAttributes attrs =
                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        HttpSession session = attrs.getRequest().getSession(false);
                        if (session != null) {
                            String jwt = (String) session.getAttribute("JWT_TOKEN");
                            if (jwt != null) {
                                request = ClientRequest.from(request)
                                        .header("Authorization", "Bearer " + jwt)
                                        .build();
                            }
                        }
                    }
                    return next.exchange(request);
                })
                .build();
    }
}
