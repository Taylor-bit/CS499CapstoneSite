package com.example.cs360_weighttracker.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs360_weighttracker.entities.User;
import com.example.cs360_weighttracker.viewmodels.UserViewModel;

// Base class for other activities to reduce repeated code
public class WeightTrackerActivity  extends AppCompatActivity {
    protected UserViewModel userViewModel;
    protected User user;

    protected void goToActivity(Activity activity) {
        Intent intent;
        switch (activity) {
            case HOME:
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
                return;
            case SMS_PERMISSION:
                intent = new Intent(this, SmsPermissionActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
                return;
            case LOGIN:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
                return;
        }
    }
    protected static enum Activity
    {
        HOME,
        SMS_PERMISSION,
        LOGIN
    }
}
