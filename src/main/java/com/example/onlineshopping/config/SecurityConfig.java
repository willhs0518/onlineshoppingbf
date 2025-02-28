package com.example.onlineshopping.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== Security Config Initialization ===");
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints that don't require authentication
                        .antMatchers("/signup", "/login", "/signup/admin").permitAll()

                        // Admin-specific endpoints (ROLE_0 is admin)
                        .antMatchers(HttpMethod.POST, "/products").hasAuthority("ROLE_0")
                        .antMatchers(HttpMethod.PATCH, "/products/**").hasAuthority("ROLE_0")
                        .antMatchers("/orders/*/complete").hasAuthority("ROLE_0")
                        .antMatchers("/products/profit/**").hasAuthority("ROLE_0")
                        .antMatchers("/products/popular/**").hasAuthority("ROLE_0")

                        // User-specific endpoints (ROLE_1 is user)
                        .antMatchers("/watchlist/**").hasAuthority("ROLE_1")
                        .antMatchers("/products/frequent/**").hasAuthority("ROLE_1")
                        .antMatchers("/products/recent/**").hasAuthority("ROLE_1")

                        // Shared endpoints with different behavior based on role
                        .antMatchers("/products/all", "/products/*", "/orders/**").authenticated()

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}