package com.greet.api.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/");
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/api"); // sets the prefix for messages that are handled by @MessageMapping annotated methods
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOrigins("http://localhost:8100"); // adds a WebSocket endpoint at the specified URL
        registry.addEndpoint("/chat").setAllowedOrigins("http://localhost:8100").withSockJS(); // adds a SockJS fallback option for clients that don't support WebSockets
    }
}



