package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class SettingsFragment extends EcareZoneBaseFragment implements AdapterView.OnItemSelectedListener {

    private Spinner mSpinner = null;
    private ArrayAdapter<CharSequence> mSpinnerAdapter = null;

    @Override
    protected String getCallerName() {
        return SettingsFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_settings, container, false);
        mSpinner = (Spinner) view.findViewById(R.id.country_spinner);
        mSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.country_array, R.layout.country_spinner_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
