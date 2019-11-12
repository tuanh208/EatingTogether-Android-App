package com.tyy.eatingtogether;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meal")
    List<Meal> getAllMeals();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(Meal... meals);

    @Query("DELETE FROM meal WHERE id = :i")
    void deleteById(int i);

    @Insert
    void insertAll(Meal... meals);
}
