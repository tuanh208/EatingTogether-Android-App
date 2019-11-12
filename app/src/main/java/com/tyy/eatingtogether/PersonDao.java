package com.tyy.eatingtogether;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PersonDao {
    @Query("SELECT * FROM person")
    List<Person> getAllPersons();

    @Query("SELECT * FROM person WHERE name LIKE :name")
    List<Person> getPersons(String name);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(Person... persons);

    @Query("DELETE FROM person WHERE name LIKE :name")
    void deleteByName(String name);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Person... persons);
}
