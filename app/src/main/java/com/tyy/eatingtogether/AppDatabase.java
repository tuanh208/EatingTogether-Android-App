package com.tyy.eatingtogether;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {FoodItem.class, Meal.class, Person.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FoodItemDao foodItemDao();
    public abstract MealDao mealDao();
    public abstract PersonDao personDao();
}
