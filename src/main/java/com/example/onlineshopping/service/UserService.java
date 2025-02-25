package com.example.onlineshopping.service;

import com.example.onlineshopping.config.JwtProvider;
import com.example.onlineshopping.dao.UserDao;
import com.example.onlineshopping.domain.hibernate.UserHibernate;
import com.example.onlineshopping.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Transactional
    public void register(UserHibernate user) {

        if(userDao.findByUsername(user.getUsername()) != null) {
            System.out.println("ERROR: Username already exists");
            throw new IllegalArgumentException("Username already exists");
        }
        if(userDao.findByEmail(user.getEmail()) != null) {
            System.out.println("ERROR: Email already exists");
            throw new IllegalArgumentException("Email already exists");
        }

        // Ensure role is set correctly
        if (user.getUsername().startsWith("admin")) {
            user.setRole(0);
        } else {
            user.setRole(1);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Transactional
    public void registerAdmin(UserHibernate user) {
        System.out.println("UserService: registerAdmin called with role: " + user.getRole());

        if(userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if(userDao.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Set admin role
        if (user.getRole() != 0) {user.setRole(0);}
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save and get the returned user
        userDao.save(user);

    }

    @Transactional
    public String login(String username, String password) {
        UserHibernate user = userDao.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        System.out.println("User role: " + user.getRole());

        UserDetails userDetails = buildUserDetails(user);
        System.out.println("Built UserDetails with authorities: " + userDetails.getAuthorities());

        // Generate JWT with role included
        return jwtProvider.createToken(userDetails, user.getRole());
    }

    private UserDetails buildUserDetails(UserHibernate user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user.getRole()))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Integer role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public UserHibernate getUserByUsername(String username) {
        UserHibernate user = userDao.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user;
    }


}
