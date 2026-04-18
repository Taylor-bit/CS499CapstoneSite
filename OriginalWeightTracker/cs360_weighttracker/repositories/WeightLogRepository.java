package com.example.cs360_weighttracker.repositories;

import android.content.Context;

import androidx.room.Room;

import com.example.cs360_weighttracker.WeightTrackerDatabase;
import com.example.cs360_weighttracker.daos.WeightLogDao;
import com.example.cs360_weighttracker.entities.WeightLog;

import java.util.List;

public class WeightLogRepository {
    private static WeightLogRepository weightLogRepo;
    private final WeightLogDao weightLogDao;

    public static WeightLogRepository getInstance(Context context) {
        if (weightLogRepo == null) {
            weightLogRepo = new WeightLogRepository(context);
        }
        return  weightLogRepo;
    }

    private WeightLogRepository(Context context) {
        WeightTrackerDatabase db = Room.databaseBuilder(context, WeightTrackerDatabase.class, "weight_tracker.db")
                .allowMainThreadQueries()
                .build();

        weightLogDao = db.weightLogDao();
    }

    public void addWeightLog(WeightLog weightLog) {
        long weightLogId = weightLogDao.addWeightLog(weightLog);
        weightLog.setId(weightLogId);
    }

    public List<WeightLog> getWeightLogs(long userId) {
        return weightLogDao.getWeightLogs(userId);
    }

    public void deleteWeightLog(WeightLog weightLog) {
        weightLogDao.deleteWeightLog(weightLog);
    }
}
