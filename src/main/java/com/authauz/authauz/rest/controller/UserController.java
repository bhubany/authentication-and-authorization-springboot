package com.authauz.authauz.rest.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authauz.authauz.common.AppScopes;
import com.authauz.authauz.security.annotation.Authorize;
import com.authauz.authauz.service.impl.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("")
    @Authorize(scope = AppScopes.CUSTOMER_ALL)
    @Authorize(scope = AppScopes.SELLER_ADMIN)
    public ResponseEntity<Object> createUser() {
        var users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @Authorize(scope = AppScopes.SELLER_ADMIN)
    @Authorize(scope = AppScopes.SELLER_MARKETING)
    @GetMapping("")
    public ResponseEntity<Object> getUsers() {
        var users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @Authorize(scope = AppScopes.SELLER_ALL)
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable UUID id) {
        var users = userService.getUser(id);
        return ResponseEntity.ok(users);
    }

    @Authorize(scope = AppScopes.CUSTOMER_ALL)
    @Authorize(scope = AppScopes.SELLER_ADMIN)
    @Authorize(scope = AppScopes.SELLER_CSR)
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID id) {
        var users = userService.updateUser(id);
        return ResponseEntity.ok(users);
    }

    @Authorize(scope = AppScopes.SELLER_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
        var users = userService.deleteUser(id);
        return ResponseEntity.ok(users);
    }

}
