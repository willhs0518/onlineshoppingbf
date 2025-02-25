package com.example.onlineshopping.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
    // This annotation injects the value from your application.properties/yml
    // The key is used to sign the JWT token
    @Value("${security.jwt.token.key}")
    private String key;

    /**
     * Creates a JWT token for a given user
     * @param userDetails The Spring Security user object containing username and authorities
     * @return A signed JWT token containing the user's information
     */

    public String createToken(UserDetails userDetails, int role) {
        System.out.println("Creating token for user: " + userDetails.getUsername());
        System.out.println("Using key: " + key);  // Ensure key is properly initialized

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("role", role);  // Store role as an integer (0 for Admin, 1 for User)

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        System.out.println("Generated token: " + token);
        return token;
    }

    public int extractRole(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("role", Integer.class); // Extract role as an integer
    }


    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token: " + token);
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            System.out.println("Token is valid");
            return true;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public UserDetails getUserDetailsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities;

        // Try to get role first (new format)
        try {
            int role = claims.get("role", Integer.class);
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        } catch (Exception e) {
            // Fallback to old permissions format
            authorities = getAuthoritiesFromClaims(claims);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(claims.getSubject())
                .password("") // No need for password here
                .authorities(authorities)
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        List<Map<String, String>> permissions = (List<Map<String, String>>) claims.get("permissions");
        return permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());
    }
}