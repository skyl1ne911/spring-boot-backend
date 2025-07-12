package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    @Query("SELECT o2.provider FROM User u JOIN u.oauth2User o2 WHERE u.email = :email")
    Optional<String> findProvider(@Param("email") String email);

    @Query("SELECT u FROM User u")
    List<User> getAllUsers();

}
