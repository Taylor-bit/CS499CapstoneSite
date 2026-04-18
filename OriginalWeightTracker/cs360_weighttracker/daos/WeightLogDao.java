package com.example.cs360_weighttracker.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cs360_weighttracker.entities.WeightLog;

import java.util.List;

@Dao
public interface WeightLogDao {
    @Query("SELECT * FROM weight_logs WHERE user_id = :userId ORDER BY date desc")
    List<WeightLog> getWeightLogs(long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addWeightLog(WeightLog weightLog);

    @Update
    void updateWeightLog(WeightLog weightLog);

    @Delete
    void deleteWeightLog(WeightLog weightLog);
}
