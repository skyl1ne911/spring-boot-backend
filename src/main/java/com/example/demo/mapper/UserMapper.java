package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;

public class UserMapper {

    public static User mapToUser(UserDto userDto) {
        return new User(
                userDto.getUsername(),
                userDto.getEmail()
        );
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getEmail()
        );
    }

    public static void updateUser(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
    }

}
