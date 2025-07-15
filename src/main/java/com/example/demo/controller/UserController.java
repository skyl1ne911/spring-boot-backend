package com.example.demo.controller;

import com.example.demo.dto.SigninRequestDto;
import com.example.demo.dto.SignupRequestDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.cloudinary.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/public")
public class UserController {
    public static final String HOME_PAGE = "/web";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/home")
    public String userAccess() {
        return "Welcome to our site";
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        log.info("All users received!");
        return userRepository.getAllUsers();
    }

    @GetMapping("/get-user")
    public User getUser(String email) {
        return userRepository.findUserByEmail(email).orElse(null);
    }

    @PostMapping("/signup")
    @SneakyThrows
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signup, HttpServletRequest request, HttpServletResponse response) {
        Authentication responseResult = userService.signup(signup, request, response);
        return ResponseEntity.ok().body(responseResult.getPrincipal());
    }

    @PostMapping("/signin")
    @SneakyThrows
    public ResponseEntity<?> signin(@RequestBody SigninRequestDto signin, HttpServletRequest request, HttpServletResponse response) {
        String res = userService.signin(signin, request, response);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/oauth2/login")
    public String oauth2Login() {
        return null;
    }

    @PostMapping("/save-img")
    public ResponseEntity<?> saveImg(@RequestBody String img) {
        try {
            return ResponseEntity.ok(cloudinaryService.uploadImage(img));
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/get-img")
    public ResponseEntity<?> getImg(@RequestParam String id) {
        try {
            String result = cloudinaryService.loadImage(id);
            return ResponseEntity.ok(Map.of("url", result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

}
