package com.rebalance.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CognitoClient {
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.client-id}")
    private String clientId;

    public String signUpUser(String username, String password, String email) {
        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(username)
                    .password(password)
                    .userAttributes(
                            AttributeType.builder().name("email").value(email).build()
                            // AttributeType.builder().name("email_verified").value("true").build()
                    )
                    .build();

            SignUpResponse response = cognitoClient.signUp(signUpRequest);

            return response.userSub();
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Failed to sign up user: " + e.getMessage(), e);
        }
    }

    // Authenticate a user and retrieve a Cognito token
    public String authenticate(String email, String password) {
        try {
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", password
                    ))
                    .build();
    
            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);
            return authResponse.authenticationResult().idToken();
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Failed to authenticate user in Cognito: " + e.getMessage(), e);
        }
    }
}
