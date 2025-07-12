package com.example.demo.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;

    public UserDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
