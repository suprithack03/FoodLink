package com.foodlink.foodlink.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Registration / profile endpoints
                         */
                        .requestMatchers(
                                "/api/donors",
                                "/api/donors/**",
                                "/api/ngos",
                                "/api/ngos/**"
                        ).permitAll()

                        /*
                         * Only DONOR can create food posts.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/food-posts"
                        ).hasRole("DONOR")

                        /*
                         * Request creation is an NGO action.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/requests"
                        ).hasRole("NGO")

                        /*
                         * NGO can view its own requests.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/requests/my"
                        ).hasRole("NGO")

                        /*
                         * DONOR can view requests for its food posts.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/requests/my-food-posts"
                        ).hasRole("DONOR")

                        /*
                         * DONOR accepts, rejects, or completes
                         * requests on their food posts.
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/requests/*/status"
                        ).hasRole("DONOR")

                        /*
                         * Everything else requires authentication.
                         */
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> {});

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}