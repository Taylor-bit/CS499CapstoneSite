package com.example.cs360_weighttracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.cs360_weighttracker.daos.UserDao;
import com.example.cs360_weighttracker.daos.WeightLogDao;
import com.example.cs360_weighttracker.entities.User;
import com.example.cs360_weighttracker.entities.WeightLog;

@Database(entities = { User.class, WeightLog.class }, version = 2)
public abstract class WeightTrackerDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract WeightLogDao weightLogDao();
}
