package com.example.cs360_weighttracker.daos;

import androidx.room.*;

import com.example.cs360_weighttracker.entities.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE _id = :id")
    User getUser(long id);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User loginUser(String username, String password);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}