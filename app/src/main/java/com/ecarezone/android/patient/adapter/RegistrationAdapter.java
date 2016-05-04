package com.ecarezone.android.patient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;

import java.util.ArrayList;

/**
 * Created by L&T Technology Services  on 2/20/2016.
 */
public class RegistrationAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private String[] items;
    private String[] itemCodes;
    private String[] mLanguageArr;
    private String[] mCountryArr;
    private String mLanguage;
    private String mCountry;
    private boolean country;

    public RegistrationAdapter(Context context, int resource, String[] items, String[] itemCodes, boolean country) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.itemCodes = itemCodes;
        this.country = country;

        mLanguageArr = context.getResources().getStringArray(R.array.language_local_array);
        mCountryArr = context.getResources().getStringArray(R.array.country_code_array);

        SharedPreferences langPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        mLanguage = langPreferences.getString(Constants.LANGUAGE, "");
        SharedPreferences countryPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        mCountry = countryPreferences.getString(Constants.COUNTRY, "");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(resource, null);
        TextView itemView = (TextView) v.findViewById(R.id.text1);
        itemView.setText(items[position]);
        itemView.setTag(itemCodes[position]);

        if(country) {
            if (mCountry.equalsIgnoreCase(mCountryArr[position])) {
                itemView.setBackgroundResource(R.drawable.circle_blue_complete);
            }
        } else {
            if (mLanguage.equalsIgnoreCase(mLanguageArr[position])) {
                itemView.setBackgroundResource(R.drawable.circle_blue_complete);
            }
        }


        return v;
    }
}
