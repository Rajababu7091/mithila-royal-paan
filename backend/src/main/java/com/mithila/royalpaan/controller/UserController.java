package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin/staff")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<User>> getStaffMembers() {
        return ResponseEntity.ok(userService.getAllStaff());
    }

    @PutMapping("/admin/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer id, @RequestParam("roleName") String roleName) {
        try {
            User updated = userService.updateUserRole(id, roleName);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
