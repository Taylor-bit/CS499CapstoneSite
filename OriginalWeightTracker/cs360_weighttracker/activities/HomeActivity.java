package com.example.cs360_weighttracker.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs360_weighttracker.R;
import com.example.cs360_weighttracker.entities.WeightLog;
import com.example.cs360_weighttracker.viewmodels.UserViewModel;
import com.example.cs360_weighttracker.viewmodels.WeightLogViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Home screen: show goal weight, list of weight logs, and let the user add new logs
public class HomeActivity extends WeightTrackerActivity {
    // ViewModels & Data
    private WeightLogViewModel weightLogViewModel;
    private long userId;
    private List<WeightLog> logs = new ArrayList<>();

    // View components
    private EditText weightET, dateET;
    private TextView goalWeightTV;
    private ImageButton editGoalBtn;
    private Button saveNewLogBtn;
    private RecyclerView rv;
    private WeightLogAdapter adapter;

    // Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Load user + view models
        userId = getIntent().getLongExtra("userId", -1);
        userViewModel = new UserViewModel(getApplication());
        user = userViewModel.getUser(userId);
        weightLogViewModel = new WeightLogViewModel(getApplication());

        initViewComponents();
        setupRecycler();
        setupDatePicker();
        setupGoalEditor();
        setupAddButton();

        loadAndRender();
    }

    // Setup helpers
    private void initViewComponents() {
        rv = findViewById(R.id.rvGrid);
        weightET = findViewById(R.id.etWeight);
        dateET = findViewById(R.id.etDate);
        saveNewLogBtn = findViewById(R.id.btnAddWeightLog);
        goalWeightTV = findViewById(R.id.tvGoalWeight);
        editGoalBtn = findViewById(R.id.btnEditGoal);

        updateGoalUI();
    }

    private void setupRecycler() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeightLogAdapter(log -> {
            weightLogViewModel.deleteWeightLog(log);
            loadAndRender();
        });
        rv.setAdapter(adapter);
    }

    private void setupDatePicker() {
        dateET.setFocusable(false);
        dateET.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, yy, mm, dd) -> {
                String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", yy, mm + 1, dd);
                dateET.setText(selectedDate);
            }, y, m, d).show();
        });
    }

    private void setupGoalEditor() {
        editGoalBtn.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setHint("e.g., 160.0");
            input.setText(user.getGoalWeight() == 0f ? "" : String.valueOf(user.getGoalWeight()));

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Edit goal weight")
                    .setView(input)
                    .setPositiveButton("Save", (d, which) -> onSaveGoal(input.getText().toString().trim()))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupAddButton() {
        saveNewLogBtn.setOnClickListener(v -> onAddNewLog());
    }

    // UI Actions
    private void onSaveGoal(String txt) {
        if (txt.isEmpty()) return;
        try {
            BigDecimal bd = new BigDecimal(txt).setScale(2, RoundingMode.HALF_UP);
            float gw = bd.floatValue();
            user.setGoalWeight(gw);
            userViewModel.updateUser(user);
            updateGoalUI();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void onAddNewLog() {
        String date = dateET.getText().toString().trim();
        String w = weightET.getText().toString().trim();
        if (date.isEmpty() || w.isEmpty()) {
            Toast.makeText(this, "Enter weight and date", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight;
        try {
            weight = Float.parseFloat(w);
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Invalid weight", Toast.LENGTH_SHORT).show();
            return;
        }

        WeightLog newWeightLog = new WeightLog(user.getId(), weight, date);
        weightLogViewModel.createWeightLog(newWeightLog);

        // clear inputs
        weightET.setText("");
        dateET.setText("");

        // Check goal condition using new vs latest previous
        WeightLog latestBefore = getLatestLog(logs);
        float goalWeight = user.getGoalWeight();
        if (weight == goalWeight
                || latestBefore != null && goalWeightReached(goalWeight, latestBefore.getWeight(), newWeightLog.getWeight())) {
            Toast.makeText(
                    this,
                    "Congratulations! You've reached your goal weight!",
                    Toast.LENGTH_SHORT).show();
            sendGoalSmsIfPossible(user.getPhoneNumber());
        }

        loadAndRender();
    }

    // Data/Rendering
    private void loadAndRender() {
        logs = weightLogViewModel.getWeightLogs(userId);
        adapter.setWeightLogs(logs);
    }

    private void updateGoalUI() {
        goalWeightTV.setText(String.format(Locale.US, "%.1f lbs", user.getGoalWeight()));
    }

    // Goal logic
    private static WeightLog getLatestLog(List<WeightLog> logs) {
        if (logs == null || logs.isEmpty()) return null;
        WeightLog latest = logs.get(0);
        for (int i = 1; i < logs.size(); i++) {
            WeightLog cur = logs.get(i);
            if (cur.getDate().compareTo(latest.getDate()) > 0) {
                latest = cur;
            }
        }
        return latest;
    }

    // returns true if goal weight met/passed
    private static boolean goalWeightReached(float goal, float newWeight, float lastWeight) {
        return newWeight == goal
                || newWeight > goal && lastWeight < goal
                || newWeight < goal && lastWeight > goal;
    }

    // send congratulations sms notification
    private void sendGoalSmsIfPossible(String rawNumber) {
        if (rawNumber == null || rawNumber.trim().isEmpty()) return;

        try {
            // ensure the phone number is in a valid format
            String dest = android.telephony.PhoneNumberUtils.normalizeNumber(rawNumber);
            if (dest == null || dest.isEmpty()) return;
            SmsManager sms = getSystemService(SmsManager.class);
            if (sms != null)
                sms.sendTextMessage(dest, null,
                        "Congrats! You've reached your goal weight!", null, null);
        } catch (SecurityException | IllegalArgumentException | IllegalStateException ignored) {
            // sms may fail, if so, display the Toast without taking out the whole app
        }
    }

    // Recycler internals
    private static class WeightLogAdapter extends RecyclerView.Adapter<WeightLogAdapter.WeightLogHolder> {
        interface OnDeleteClick { void onDelete(WeightLog log); }

        private final List<WeightLog> weightLogs = new ArrayList<>();
        private final OnDeleteClick onDelete;

        WeightLogAdapter(OnDeleteClick onDelete) { this.onDelete = onDelete; }

        void setWeightLogs(List<WeightLog> newItems) {
            weightLogs.clear();
            if (newItems != null) weightLogs.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public WeightLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_weight_row, parent, false);
            return new WeightLogHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull WeightLogHolder holder, int position) {
            WeightLog log = weightLogs.get(position);
            holder.bind(log);
            holder.deleteButton.setOnClickListener(v -> { if (onDelete != null) onDelete.onDelete(log); });
        }

        @Override
        public int getItemCount() { return weightLogs.size(); }

        static class WeightLogHolder extends RecyclerView.ViewHolder {
            final TextView weightTextView;
            final TextView dateTextView;
            final Button deleteButton;

            WeightLogHolder(@NonNull View itemView) {
                super(itemView);
                weightTextView = itemView.findViewById(R.id.tvWeight);
                dateTextView = itemView.findViewById(R.id.tvDate);
                deleteButton = itemView.findViewById(R.id.btnDeleteItemWeightRow);
            }

            void bind(WeightLog weightLog) {
                weightTextView.setText(String.format(Locale.US, "%.2f", weightLog.getWeight()));
                dateTextView.setText(weightLog.getDate());
            }
        }
    }
}
