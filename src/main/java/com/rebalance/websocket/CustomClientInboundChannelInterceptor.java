package com.rebalance.websocket;

import com.rebalance.entity.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomClientInboundChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                throw new RebalanceException(RebalanceErrorType.RB_401);
            }
            if (!authHeader.startsWith("Bearer") || authHeader.length() <= 7) {
                throw new RebalanceException(RebalanceErrorType.RB_401);
            }

            String jwt = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null) {
                // if user is not authenticated, get it from database
                User user = (User) userDetailsService.loadUserByUsername(userEmail);
                // then check if token is valid
                if (jwtService.isTokenValid(jwt, user)) {
                    // update MessageAccessor with authentication
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, jwt, user.getAuthorities()
                    );
                    accessor.setUser(authToken);
                }
            }
        }
        return message;
    }
}
