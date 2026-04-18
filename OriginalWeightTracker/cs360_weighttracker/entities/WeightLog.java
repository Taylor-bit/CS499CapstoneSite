package com.example.cs360_weighttracker.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

@Entity(tableName = "weight_logs", foreignKeys = @ForeignKey(entity = User.class, parentColumns = "_id", childColumns = "user_id", onDelete = CASCADE))
public class WeightLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "weight")
    private float weight;

    @ColumnInfo(name = "date")
    private String date;

    @Ignore
    public WeightLog(long userId, float weight, String date) {
        this.userId = userId;
        this.weight = weight;
        this.date = date;
    }

    public WeightLog() { }

    public long getId() { return id; }

    public long getUserId() { return userId; }

    public float getWeight() { return weight; }

    public String getDate() { return date; }

    public void setId(long id) { this.id = id; }

    public void setUserId(long userId) { this.userId = userId; }

    public void setWeight(float weight) { this.weight = weight; }

    public void setDate(String date) { this.date = date; }

    @Ignore
    public static boolean isValidDate(String date) {
        if (date == null) return false;

        // Strict formatter to enforce exact format and valid calendar dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate.parse(date, formatter);
            return true; // valid date
        } catch (DateTimeParseException e) {
            return false; // invalid format or invalid calendar date
        }
    }
}
