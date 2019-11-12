package com.tyy.eatingtogether;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class FoodItem {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "food_name")
    String name;
    @ColumnInfo(name = "quantity")
    Double quantity;
    @ColumnInfo(name = "unit")
    String unit;
    @ColumnInfo(name = "price")
    Double price;
    @ColumnInfo(name = "buyer")
    String buyer;
    Boolean chosen = false;

    public FoodItem() {
    }

    public FoodItem(String n, Double p, String b) {
        name = n;
        price = p;
        buyer = b;
    }

    public FoodItem(String n, Double p, Double q, String u, String b) {
        name = n;
        quantity = q;
        unit = u;
        price = p;
        buyer = b;
    }

    public void choose() {
        this.chosen = true;
    }

    public void unchoose() { this.chosen = false; }

}
