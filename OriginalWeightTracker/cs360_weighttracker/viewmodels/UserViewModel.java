package com.example.cs360_weighttracker.viewmodels;

import android.app.Application;

import com.example.cs360_weighttracker.entities.User;
import com.example.cs360_weighttracker.repositories.UserRepository;

public class UserViewModel {
    private UserRepository userRepo;
    public UserViewModel(Application application) {
        userRepo = UserRepository.getInstance(application.getApplicationContext());
    }

    public User loginUser(String username, String password) {
        return userRepo.loginUser(username, password);
    }

    public User getUser(long userId) {
        return userRepo.getUser(userId);
    }

    public void createUser(User user) {
        userRepo.addUser(user);
    }

    public void updateUser(User user) {
        userRepo.updateUser(user);
    }
}
