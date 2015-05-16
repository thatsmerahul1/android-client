package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;


/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class RegistrationFragment extends EcareZoneBaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static RegistrationFragment newInstance() {
        return  new RegistrationFragment();
    }

    private Spinner mSpinner = null;
    private ArrayAdapter<CharSequence> mSpinnerAdapter = null;
    private View mButtonRegister = null;
    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;

    @Override
    protected String getCallerName() {
        return RegistrationFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_registration, container, false);
        mButtonRegister = view.findViewById(R.id.button_register);
        mButtonRegister.setOnClickListener(this);
        mButtonRegister.setEnabled(false);
        mEditTextUsername = (EditText)view.findViewById(R.id.edit_text_registration_username);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = false;
                enable = ((!TextUtils.isEmpty(s))
                        && (!TextUtils.isEmpty(mEditTextPassword.getEditableText().toString())));
                if(mButtonRegister != null) {
                    mButtonRegister.setEnabled(enable);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEditTextPassword = (EditText)view.findViewById(R.id.edit_text_registration_password);
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = false;
                enable = ((!TextUtils.isEmpty(s))
                        && (!TextUtils.isEmpty(mEditTextUsername.getEditableText().toString())));
                if(mButtonRegister != null) {
                    mButtonRegister.setEnabled(enable);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSpinner = (Spinner) view.findViewById(R.id.country_spinner);
        mSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.country_array, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setPrompt("Country");
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.button_register) {
            //TODO register
            /*
            final Activity activity = getActivity();
            if(activity != null) {
                activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
            }
            */
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
