package com.example.onlineshopping.domain.hibernate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserHibernate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    @JsonProperty("email")
    private String email;

    @Column(name = "password", nullable = false)
    @JsonProperty("password")  // Add this
    private String password;

    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "username", nullable = false, unique = true)
    @JsonProperty("username")  // Add this
    private String username;

    public Long getUserId() {return userId;}
    public String getUsername() { return username;}
    public String getPassword() { return password;}
    public String getEmail() { return email;}
    public int getRole() { return role;}

    public void setPassword(String password) { this.password = password;}
    public void setEmail(String email) { this.email = email;}
    public void setUsername(String username) { this.username = username;}
    public void setRole(Integer role) { this.role = role;}

    public UserHibernate() {}

    public UserHibernate(String username, String email, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
        // this.role = username.startsWith("admin") ? 0 : 1;  // Set default role based on username
    }
}
