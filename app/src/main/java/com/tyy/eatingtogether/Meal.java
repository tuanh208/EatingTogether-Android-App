package com.tyy.eatingtogether;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Meal {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "meal_name")
    String name;
    ArrayList<Integer> food_id;
    ArrayList<String> participant;
    Double price;

    public Meal() {
    }

    public Meal(String name) {
        this.name = name;
        this.food_id = new ArrayList<>();
        this.participant = new ArrayList<>();
        this.price = 0.0;
    }

    public Meal(String name, ArrayList<FoodItem> food_list, ArrayList<Person> persons) {
        this.name = name;
        this.food_id = new ArrayList<>();
        this.participant = new ArrayList<>();
        price = 0.0;

        for (int i = 0; i < food_list.size(); i ++) {
            this.food_id.add(food_list.get(i).id);
            price += food_list.get(i).price;
        }

        for (int j = 0; j < persons.size(); j ++) {
            this.participant.add(persons.get(j).name);
            //persons.get(j).addBalance(-price/persons.size());
        }
    }
}
