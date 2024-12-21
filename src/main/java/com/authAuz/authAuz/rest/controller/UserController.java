package com.authAuz.authAuz.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authAuz.authAuz.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<Object> getUsers() {
        var users = userService.getUsers();
        return ResponseEntity.ok(users);
    }
}
