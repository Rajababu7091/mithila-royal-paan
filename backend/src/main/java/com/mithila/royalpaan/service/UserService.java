package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Role;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.repository.RoleRepository;
import com.mithila.royalpaan.repository.UserRepository;
import com.mithila.royalpaan.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setProvider("LOCAL");
        user.setEmailVerified(false);

        // Assign standard ROLE_CUSTOMER
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));
        user.setRole(customerRole);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllStaff() {
        return userRepository.findByRoleName("ROLE_STAFF");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserRole(Integer userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
    }
}
