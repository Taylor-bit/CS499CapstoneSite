package com.example.cs360_weighttracker.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "goal_weight")
    private float goalWeight;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    public User() { }

    @Ignore
    public User(long id, @NonNull String username, @NonNull String password,
                float goalWeight, @NonNull String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.goalWeight = goalWeight;
        this.phoneNumber = phoneNumber;
    }

    @Ignore
    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
        this.goalWeight = 0.0f;
        this.phoneNumber = "";
    }

    public long getId() { return id; }

    @NonNull
    public String getUsername() { return username; }

    @NonNull
    public String getPassword() { return password; }

    public float getGoalWeight() { return goalWeight; }

    @NonNull
    public String getPhoneNumber() { return phoneNumber; }

    public void setId(long id) { this.id = id; }

    public void setUsername(@NonNull String username) { this.username = username; }

    public void setPassword(@NonNull String password) { this.password = password; }

    public void setGoalWeight(float goalWeight) { this.goalWeight = goalWeight; }

    public void setPhoneNumber(@NonNull String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Ignore
    public static boolean isValidPhone(String phoneNumber) {
        if (phoneNumber == null) return false;
        String digits = phoneNumber.replaceAll("\\D", "");
        return digits.length() == 10 || (digits.length() == 11 && digits.startsWith("1"));
    }
}
