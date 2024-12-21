package com.authAuz.authAuz.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void saveUser() {
        System.out.println("Saving user");
    }

    public String getUsers() {
        return "getUsers";
    }

    public String getUser() {
        return "getUser";
    }

}
