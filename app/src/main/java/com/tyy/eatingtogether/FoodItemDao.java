package com.tyy.eatingtogether;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FoodItemDao {
    @Query("SELECT * FROM fooditem")
    List<FoodItem> getAllFoods();

    @Query("SELECT * FROM fooditem WHERE id = :i")
    List<FoodItem> getFoodFromId(int i);

    @Query("SELECT * FROM fooditem WHERE chosen = 1")
    List<FoodItem> getAllChosenFoods();

    @Query("SELECT * FROM fooditem WHERE chosen = 0")
    List<FoodItem> getAllUnchosenFoods();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(FoodItem... foodItems);

    @Insert
    void insertAll(FoodItem... foodItems);

    @Delete
    void delete(FoodItem foodItem);
}
