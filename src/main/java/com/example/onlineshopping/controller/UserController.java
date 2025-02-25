package com.example.onlineshopping.controller;

import com.example.onlineshopping.domain.hibernate.UserHibernate;
import com.example.onlineshopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling user authentication operations including registration and login.
 * All endpoints are prefixed with /api/users for consistent API structure.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin  // Enables cross-origin requests, important for frontend integration
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Handles user registration requests.
     * Validates the incoming user data and creates a new user account if validation passes.
     * Returns 400 BAD REQUEST if username/email already exists.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserHibernate user,
                                        @RequestParam(required = false, defaultValue = "false") boolean isAdmin) {
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be empty");
        }

        if (isAdmin) {
            user.setRole(0);
            userService.registerAdmin(user);
            return ResponseEntity.ok("Admin user registered successfully with role 0");
        } else {
            userService.register(user);
            return ResponseEntity.ok("Regular user registered successfully with role 1");
        }
    }


    /**
     * Handles user login requests.
     * Authenticates user credentials and returns a JWT token if successful.
     * Returns 401 UNAUTHORIZED if credentials are invalid.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserHibernate user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        int role = userService.getUserByUsername(user.getUsername()).getRole();  // Get role
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role); // Include role in response
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Since JWT is stateless, the actual token invalidation happens client-side
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}


