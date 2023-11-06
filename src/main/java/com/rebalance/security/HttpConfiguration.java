package com.rebalance.security;

import com.rebalance.controller.APIVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class HttpConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // ConnectivityController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/connect/test").permitAll()

                        // AuthenticationController
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/user/logout").authenticated()

                        // GroupController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*/users").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/group").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/group/users").authenticated()

                        // GroupExpenseController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*/expenses").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/group/expenses").authenticated()
                        .requestMatchers(HttpMethod.PUT, APIVersion.current + "/group/expenses").authenticated()
                        .requestMatchers(HttpMethod.DELETE, APIVersion.current + "/group/expenses/*").authenticated()

                        // ImageController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/expense/*/image").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/expense/*/preview").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/expense/*/image").authenticated()

                        // NotificationController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/user/*/notifications").authenticated()

                        // PersonalExpenseController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/personal/expenses").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/personal/expenses").authenticated()
                        .requestMatchers(HttpMethod.PUT, APIVersion.current + "/personal/expenses").authenticated()
                        .requestMatchers(HttpMethod.DELETE, APIVersion.current + "/personal/expenses/*").authenticated()

                        //UserController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/user/email/*").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/user/groups").authenticated()

//                .requestMatchers(HttpMethod.GET, "/statistics").hasAuthority(UserRole.ADMIN.name())
        );

        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());

        return http.build();
    }
}
