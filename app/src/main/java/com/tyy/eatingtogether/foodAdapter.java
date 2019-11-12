package com.tyy.eatingtogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class foodAdapter extends ArrayAdapter<FoodItem> {
    public foodAdapter(Context context, List<FoodItem> food_list) {
        super(context, 0, food_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FoodItem food_item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_food, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.foodName);
        TextView tvPrice = convertView.findViewById(R.id.foodPrice);
        TextView tvBuyer = convertView.findViewById(R.id.buyerName);
        // Populate the data into the template view using the data object
        tvName.setText(food_item.name);
        tvPrice.setText(food_item.price.toString()+"€");
        tvBuyer.setText(food_item.buyer.toString());
        // Return the completed view to render on screen
        return convertView;
    }
}
