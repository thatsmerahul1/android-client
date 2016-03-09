package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.service.EcareZoneWebService;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.ecarezone.android.patient.model.rest.SignupRequest;
import com.ecarezone.android.patient.model.rest.SignupResponse;
import com.ecarezone.android.patient.utils.EcareZoneLog;
import com.ecarezone.android.patient.utils.PasswordUtil;

import retrofit.RestAdapter;



/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class RegistrationFragment extends EcareZoneBaseFragment implements View.OnClickListener,
                                                                           AdapterView.OnItemSelectedListener,
                                                                           CompoundButton.OnCheckedChangeListener {

    public static RegistrationFragment newInstance() {
        return  new RegistrationFragment();
    }

    private Spinner mSpinner = null;
    private ArrayAdapter<CharSequence> mSpinnerAdapter = null;
    private View mButtonRegister = null;
    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private CheckBox mCheckBoxTerms = null;
    private String mSelectedCountry = null;

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
                try {
                    checkRegistrationButtonStatus(String.valueOf(s),
                            mEditTextPassword.getEditableText().toString(),
                            mCheckBoxTerms.isChecked());
                } catch (Exception e) {
                    EcareZoneLog.e(getCallerName(), e);
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
                try {
                    checkRegistrationButtonStatus(mEditTextUsername.getEditableText().toString(),
                                                 String.valueOf(s), mCheckBoxTerms.isChecked());
                } catch (Exception e) {
                    EcareZoneLog.e(getCallerName(), e);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // add editor action listener for password EditText to accept input from soft keyboard
        // Then the user need not to click login button
        mEditTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_SEND) {

                    onClick(mButtonRegister);
                    return true;
                }
                return false;
            }
        });

        mSpinner = (Spinner) view.findViewById(R.id.country_spinner);
        mSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.country_array, R.layout.country_spinner_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mCheckBoxTerms = (CheckBox)view.findViewById(R.id.checkbox_registration_terms);
        mCheckBoxTerms.setOnCheckedChangeListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.button_register) {
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextPassword.getEditableText().toString();
            if(TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(password) || (password.trim().length() < 8))) {
                Toast.makeText(v.getContext(), R.string.error_user_registration, Toast.LENGTH_LONG).show();
            } else {
                doRegistration(username,password,"SE");
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCountry = String.valueOf(mSpinner.getSelectedItem());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == null) return;;
        final int viewId = buttonView.getId();

        if(viewId == R.id.checkbox_registration_terms) {
            try {
                final String username = mEditTextUsername.getEditableText().toString();
                final String password = mEditTextPassword.getEditableText().toString();
                checkRegistrationButtonStatus(username, password, isChecked);
            } catch (Exception e) {
                EcareZoneLog.e(getCallerName(), e);
            }
        }
    }

    private void checkRegistrationButtonStatus(final String username, final String password, final boolean isChecked) {
        boolean enable = false;
        enable = ((!TextUtils.isEmpty(username))
                && (!TextUtils.isEmpty(password))
                && isChecked);
        if(mButtonRegister != null) {
            mButtonRegister.setEnabled(enable);
        }
    }

    private void doRegistration2(final String username, final String pwd) {
        final Activity activity = getActivity();

    }

    private void doRegistration(String username, String password, String country) {
        //TODO register

        DoRegistration registration = new DoRegistration();
        registration.execute(username,password,country);
    }

    private class DoRegistration extends AsyncTask<String, Void, SignupResponse> {

        @Override
        protected SignupResponse doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String hashedPassword = PasswordUtil.getHashedPassword(password);
            String country = params[2];

            SignupResponse response = EcareZoneWebService.api.signup(new SignupRequest(username, hashedPassword, 1, country, "en", "N/A", "N/A", Constants.API_KEY, Constants.deviceUnique));
            LoginInfo.userName = username;
            LoginInfo.hashedPassword = hashedPassword;
            return response;
        }

        @Override
        protected void onPostExecute(SignupResponse response) {
            if(response.status.code == 201){
                final Activity activity = getActivity();
                if(activity != null) {
                    activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                    activity.finish();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Failed to signup: "+response.status.message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
