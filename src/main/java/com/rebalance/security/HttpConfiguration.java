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
                        // Swagger
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs.yaml").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()

                        // ConnectivityController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/connect/test").permitAll()

                        // AuthenticationController
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/user/logout").authenticated()

                        // CategoryController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/personal/categories").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*/categories").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/*/sum-by-category").authenticated()

                        // GroupController
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*/users").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/group").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/group/users").authenticated()
                        .requestMatchers(HttpMethod.POST, APIVersion.current + "/group/set-favorite").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/group/*/debt-settlement").authenticated()

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
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/user/info").authenticated()
                        .requestMatchers(HttpMethod.GET, APIVersion.current + "/user/groups").authenticated()

                        .anyRequest().denyAll()

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
