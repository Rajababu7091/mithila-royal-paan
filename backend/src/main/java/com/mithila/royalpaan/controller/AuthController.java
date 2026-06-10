package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.JwtResponse;
import com.mithila.royalpaan.dto.LoginRequest;
import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.dto.RegisterRequest;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.security.CustomUserDetails;
import com.mithila.royalpaan.security.JwtUtils;
import com.mithila.royalpaan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                userDetails.getName(),
                role));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return ResponseEntity.status(401).body(new MessageResponse("Unauthorized"));
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(new MessageResponse("Authenticated user not found"));
        }

        return ResponseEntity.ok(user);
    }
}
