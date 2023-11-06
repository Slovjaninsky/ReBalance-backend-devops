package com.rebalance.security;

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
                        .requestMatchers(HttpMethod.GET, "/connect/test").permitAll()

                        // AuthenticationController
                        .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/logout").authenticated()

                        // GroupController
                        .requestMatchers(HttpMethod.GET, "/group/*/users").authenticated()
                        .requestMatchers(HttpMethod.GET, "/group/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/group").authenticated()
                        .requestMatchers(HttpMethod.POST, "/group/users").authenticated()

                        // GroupExpenseController
                        .requestMatchers(HttpMethod.GET, "/group/*/expenses").authenticated()
                        .requestMatchers(HttpMethod.POST, "/group/expenses").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/group/expenses").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/group/expenses/*").authenticated()

                        // ImageController
                        .requestMatchers(HttpMethod.GET, "/expense/*/image").authenticated()
                        .requestMatchers(HttpMethod.GET, "/expense/*/preview").authenticated()
                        .requestMatchers(HttpMethod.POST, "/expense/*/image").authenticated()

                        // NotificationController
                        .requestMatchers(HttpMethod.GET, "/user/*/notifications").authenticated()

                        // PersonalExpenseController
                        .requestMatchers(HttpMethod.GET, "/personal/expenses").authenticated()
                        .requestMatchers(HttpMethod.POST, "/personal/expenses").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/personal/expenses").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/personal/expenses/*").authenticated()

                        //UserController
                        .requestMatchers(HttpMethod.GET, "/user/email/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/groups").authenticated()

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
