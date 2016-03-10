package com.ecarezone.android.patient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ecarezone.android.patient.R;

/**
 * Created by L&T Technology Services  on 2/20/2016.
 */
public class RegistrationAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;
    String[] items;
    String[] itemCodes;

    public RegistrationAdapter(Context context, int resource, String[] items, String[] itemCodes) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.itemCodes = itemCodes;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(resource, null);
        TextView itemView = (TextView) v.findViewById(R.id.text1);
        itemView.setText(items[position]);
        itemView.setTag(itemCodes[position]);
        return v;
    }
}
