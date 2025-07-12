package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column
    private String password;

    @Column(name = "no_crypt_password")
    private String noCryptPassword;

    @Column(unique = true, nullable = false)
    private String email;

    // Cloudinary
    @Column(name = "avatar_public_id")
    private String avatar;

    @Column(name = "register_date")
    private LocalDate dateOfRegister;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "oauth2_id", referencedColumnName="sub")
    private OAuth2UserEntity oauth2User;


    @PrePersist
    protected void onCreate() {
        dateOfRegister = LocalDate.now();
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
