package com.tyy.eatingtogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class mealAdapter extends ArrayAdapter<Meal> {
    public mealAdapter(Context context, List<Meal> meal_list) {
        super(context, 0, meal_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Meal meal_item = getItem(position);
        int numPerson = meal_item.participant.size();
        Double pricePerPerson = meal_item.price/numPerson;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_meal, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.mealName);
        TextView tvPrice = convertView.findViewById(R.id.mealPrice);
        // Populate the data into the template view using the data object
        tvName.setText(meal_item.name);
        tvPrice.setText(String.format(Locale.getDefault(), "%.2f €/ %d người ăn - %.2f €/người", meal_item.price, numPerson, pricePerPerson));
        // Return the completed view to render on screen
        return convertView;
    }
}
