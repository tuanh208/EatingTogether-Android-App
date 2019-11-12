package com.tyy.eatingtogether;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

@Entity
public class Person {
   @PrimaryKey
   @android.support.annotation.NonNull
   String  name;
    Double balance;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
        this.balance = 0.0;
    }
    public void addBalance(Double price) {
        this.balance += price;
    }
}
