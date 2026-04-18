package com.example.cs360_weighttracker.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs360_weighttracker.R;
import com.example.cs360_weighttracker.entities.User;
import com.example.cs360_weighttracker.viewmodels.UserViewModel;

// Login screen: allow users to sign in and create accounts
public class LoginActivity extends WeightTrackerActivity {
    // View components
    private EditText usernameET, passwordET;
    private Button loginBtn, createBtn;

    // Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userViewModel = new UserViewModel(getApplication());

        initViewComponents();
        setupLoginButton();
        setupCreateUserButton();
    }

    // Setup helpers
    private void initViewComponents() {
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        loginBtn = findViewById(R.id.buttonLogin);
        createBtn = findViewById(R.id.buttonCreateAccount);
    }
    private void setupLoginButton() {
        loginBtn.setOnClickListener(v -> {
            user = userViewModel.loginUser(usernameET.getText().toString(), passwordET.getText().toString());
            if (user == null) {
                Toast.makeText(this, "Incorrect username/password", Toast.LENGTH_SHORT).show();
            } else {
                goToActivity(Activity.HOME);
            }
        });
    }

    private void setupCreateUserButton() {
        createBtn.setOnClickListener(v -> {
            User newUser = new User(usernameET.getText().toString(), passwordET.getText().toString());
            userViewModel.createUser(newUser);
            user = newUser;
            goToActivity(Activity.SMS_PERMISSION);
        });
    }
}
