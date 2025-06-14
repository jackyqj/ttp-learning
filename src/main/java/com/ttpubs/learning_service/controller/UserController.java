package com.ttpubs.learning_service.controller;

import com.ttpubs.learning_service.model.User;
import com.ttpubs.learning_service.security.JwtTokenUtil;
import com.ttpubs.learning_service.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5174") // 允许来自指定源的跨域请求
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/hello")
    public ResponseEntity<String> hello(HttpServletRequest request) {
        logger.info("Received request to /hello endpoint from {}", request.getRemoteAddr());

        String userAgent = request.getHeader("User-Agent");
        return ResponseEntity.ok("Hello! Your browser info: " + userAgent);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationRequest) {
        String username = registrationRequest.get("username");
        String password = registrationRequest.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        try {
            User registeredUser = userService.registerUser(username, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        Optional<User> userOptional = userService.loginUser(username, password);
        if (userOptional.isPresent()) {
            // 在实际应用中，你可能需要返回一个 JWT 或 Session ID
            String token = jwtTokenUtil.generateToken(username);
            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
    @GetMapping("/list")
    @CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true") // 允许来自指定源的跨域请求
    public ResponseEntity<?> listUsers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User {} is requesting the list of users", username);
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
}
record LoginRequest(String username, String password) {}
record LoginResponse(String token) {}