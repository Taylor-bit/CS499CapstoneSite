package com.example.cs360_weighttracker.repositories;

import android.content.Context;

import androidx.room.Room;

import com.example.cs360_weighttracker.entities.User;
import com.example.cs360_weighttracker.daos.UserDao;
import com.example.cs360_weighttracker.WeightTrackerDatabase;

public class UserRepository {
    private static UserRepository userRepo;
    private final UserDao userDao;

    public static UserRepository getInstance(Context context) {
        if (userRepo == null) {
            userRepo = new UserRepository(context);
        }
        return userRepo;
    }

    private UserRepository(Context context) {
        WeightTrackerDatabase db = Room.databaseBuilder(context, WeightTrackerDatabase.class, "weight_tracker.db")
                .allowMainThreadQueries()
                .build();

        userDao = db.userDao();
    }

    public void addUser(User user) {
        long userId = userDao.addUser(user);
        user.setId(userId);
    }

    public User getUser(long userId) {
        return userDao.getUser(userId);
    }

    public User loginUser(String username, String password) {
        return userDao.loginUser(username, password);
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }
}
