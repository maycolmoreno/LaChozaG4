package com.choza.consumochoza.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import jakarta.servlet.http.HttpSession;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Bean
    WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10));

        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
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
