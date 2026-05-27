package com.lachozag4.pisip.infraestructura.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configura el broker STOMP sobre WebSocket.
 *
 * Punto de conexión  : ws://host:8081/ws  (y ws://host:8081/ws-sockjs con SockJS)
 * Prefijo publicación: /app
 * Topics de escucha  : /topic/cocina    → notificaciones hacia el personal de cocina
 *                      /topic/camarero  → notificaciones hacia los camareros
 *
 * El cliente MAUI se conecta con ClientWebSocket (protocolo STOMP sobre WS puro).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker en memoria para topics y colas
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefijo para mensajes dirigidos a métodos @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket puro (Android emulador y clientes nativos)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // Endpoint con SockJS (fallback HTTP para navegadores)
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
