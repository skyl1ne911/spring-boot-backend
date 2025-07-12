package com.example.demo.repository;

import com.example.demo.entity.OAuth2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2UserRepository extends JpaRepository<OAuth2UserEntity, String> {



}
