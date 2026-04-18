package com.example.cs360_weighttracker.viewmodels;

import android.app.Application;

import com.example.cs360_weighttracker.entities.WeightLog;
import com.example.cs360_weighttracker.repositories.WeightLogRepository;

import java.util.List;

public class WeightLogViewModel {
    private WeightLogRepository weightLogRepo;

    public WeightLogViewModel(Application application) {
        weightLogRepo = WeightLogRepository.getInstance(application.getApplicationContext());
    }

    public void createWeightLog(WeightLog weightLog) {
        weightLogRepo.addWeightLog(weightLog);
    }

    public List<WeightLog> getWeightLogs(long userId) {
        return weightLogRepo.getWeightLogs(userId);
    }

    public void deleteWeightLog(WeightLog weightLog) {

        weightLogRepo.deleteWeightLog(weightLog);
    }
}
