package com.authAuz.authAuz.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String createUser() {
        return "User created successfully";
    }

    public String getUsers() {
        return "getUsers";
    }

    public String getUser(UUID id) {
        return "user details with id: " + id;
    }

    public String updateUser(UUID id) {
        return "user with id: " + id + " updated successfully";
    }

    public String deleteUser(UUID id) {
        return "user with id: " + id + " deleted successfully";
    }

}
