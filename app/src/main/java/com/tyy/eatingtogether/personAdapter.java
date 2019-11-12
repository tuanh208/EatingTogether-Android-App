package com.tyy.eatingtogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class personAdapter extends ArrayAdapter<Person> {
    public personAdapter(Context context, List<Person> person_list) {
        super(context, 0, person_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Person person_item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_person, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.personName);
        TextView tvPrice = convertView.findViewById(R.id.personBalance);
        // Populate the data into the template view using the data object
        tvName.setText(person_item.name);
        tvPrice.setText(String.format(Locale.getDefault(), "%.2f €", person_item.balance));
        // Return the completed view to render on screen
        return convertView;
    }
}
