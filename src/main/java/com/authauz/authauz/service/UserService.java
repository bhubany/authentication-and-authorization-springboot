package com.authauz.authauz.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String createUser() {
        return "User created successfully";
    }

    public String getUsers() {
        return "Users retrieved successfully";
    }

    public String getUser(UUID id) {
        return "User with id " + id + " retrieved successfully";
    }

    public String updateUser(UUID id) {
        return "User with ID: " + id + " updated successfully";
    }

    public String deleteUser(UUID id) {
        return "User with ID: " + id + " deleted successfully";
    }

}
