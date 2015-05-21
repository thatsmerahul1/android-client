package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;


/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class RegistrationFragment extends EcareZoneBaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner mSpinner = null;
//    Add by Lai
    private Spinner mSpinner_language = null;
    //    Add by Lai

    @Override
    protected String getCallerName() {
        return RegistrationFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_registration, container, false);
        view.findViewById(R.id.button_register).setOnClickListener(this);

        mSpinner = (Spinner) view.findViewById(R.id.country_spinner);
        ArrayAdapter<CharSequence> mSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.country_array, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setPrompt("Prompt");

//        Add by Lai
        mSpinner_language = (Spinner) view.findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> mSpinnerAdapter_language = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.language_array, android.R.layout.simple_spinner_item);
//        ArrayAdapter<CharSequence> mSpinnerAdapter_language = ArrayAdapter.createFromResource(this,R.array.language_array,android.R.layout.simple_spinner_dropdown_item);
//        mSpinnerAdapter_language.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_language.setAdapter(mSpinnerAdapter_language);
        mSpinner_language.setOnItemSelectedListener(this);
        mSpinner_language.setPrompt("Prompt");
//        Add by Lai

        return view;
    }


    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.button_register) {
            //TODO register
            final Activity activity = getActivity();
            if(activity != null) {
                activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
