package com.tyy.eatingtogether;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ArrayList<Integer> fromStringtoListInteger(String value) {
        String[] dbValues = value.split(" ");
        ArrayList<Integer> int_list = new ArrayList<>();
        for (int i = 0; i < dbValues.length; i ++) {
            int_list.add(Integer.parseInt(dbValues[i]));
        }
        return int_list;
    }

    @TypeConverter
    public static String fromArrayListtoString(ArrayList<Integer> list) {
        String value = "";
        for (int i = 0; i < list.size(); i++) {
            value = value + list.get(i).toString();
            if (i != list.size() - 1) {
                value = value + " ";
            }
        }
        return value;
    }
}
