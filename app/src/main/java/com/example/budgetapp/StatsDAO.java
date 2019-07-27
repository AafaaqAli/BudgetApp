package com.example.budgetapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StatsDAO {
    @Insert
        void insertCurrentStat(Stat ... stat);

    @Query("DELETE FROM Stats_Table WHERE uid = :id")
    int deleteStatsById(final int id);


    @Query("SELECT * FROM STATS_TABLE WHERE uid =:id")
        Stat getStatByID(final int id);

    @Query("SELECT * FROM Stats_Table ORDER BY uid ASC")
        List<Stat> getStats();

}
