package com.example.cs360_weighttracker.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.example.cs360_weighttracker.R;
import com.example.cs360_weighttracker.entities.User;
import com.example.cs360_weighttracker.viewmodels.UserViewModel;

public class SmsPermissionActivity extends WeightTrackerActivity {
    private EditText phoneET;
    private Button agreeBtn, declineBtn;

    private ActivityResultLauncher<String> requestPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        // Load user + view models
        long userId = getIntent().getLongExtra("userId", -1);
        userViewModel = new UserViewModel(getApplication());
        user = userViewModel.getUser(userId);

        initViewComponents();
        setupAgreeButton();
        setupDeclineButton();
    }

    // Setup helpers
    private void initViewComponents() {
        phoneET = findViewById(R.id.etPhone);
        agreeBtn = findViewById(R.id.btnAgree);
        declineBtn = findViewById(R.id.btnDecline);
    }

    private void setupAgreeButton() {
        agreeBtn.setOnClickListener(v -> {
            if (!deviceHasTelephony()) {
                Toast.makeText(this,
                        "No telephony on this device/emulator.", Toast.LENGTH_SHORT).show();
                goToActivity(Activity.HOME);
                return;
            }

            String phoneNumber = phoneET.getText().toString();

            if (!phoneNumber.equals(user.getPhoneNumber())){
                if (User.isValidPhone(phoneNumber)) {
                    user.setPhoneNumber(phoneNumber);
                    userViewModel.updateUser(user);
                } else {
                    Toast.makeText(this,
                            "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (hasSmsPermission()) {
                Toast.makeText(this,
                        "Permission already granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Trigger system permission dialog
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        new ActivityResultCallback<Boolean>() {
                            @Override
                            public void onActivityResult(Boolean granted) {
                                if (granted != null && granted) {
                                    Toast.makeText(SmsPermissionActivity.this,
                                            "You've opted in for SMS notifications.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SmsPermissionActivity.this,
                                            "You've opted out for SMS notifications",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .launch(Manifest.permission.SEND_SMS);
            }

            goToActivity(Activity.HOME);
        });
    }

    private void setupDeclineButton() {
        declineBtn.setOnClickListener(v -> {
            goToActivity(Activity.HOME);
        });
    }

    // Permission logic
    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean deviceHasTelephony() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }
}
