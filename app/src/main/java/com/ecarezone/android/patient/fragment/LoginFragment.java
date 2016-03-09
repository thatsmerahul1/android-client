package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.ecarezone.android.patient.model.rest.LoginRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.service.EcareZoneWebService;

import com.ecarezone.android.patient.utils.EcareZoneLog;
import com.ecarezone.android.patient.utils.PasswordUtil;

import retrofit.RestAdapter;

public class LoginFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    public static LoginFragment newInstance() {
        return  new LoginFragment();
    }

    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private View mButtonLogin = null;

    @Override
    protected String getCallerName() {
        return LoginFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_login, container, false);
        mEditTextUsername = (EditText)view.findViewById(R.id.edit_text_login_username);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkLoginButtonStatus(String.valueOf(s),
                            mEditTextPassword.getEditableText().toString());
                } catch (Exception e) {
                    EcareZoneLog.e(getCallerName(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEditTextPassword = (EditText)view.findViewById(R.id.edit_text_login_password);
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkLoginButtonStatus(String.valueOf(s),
                            mEditTextUsername.getEditableText().toString());
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

                    onClick(mButtonLogin);
                    return true;
                }
                return false;
            }
        });

        mButtonLogin = view.findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        mButtonLogin.setEnabled(false);
        view.findViewById(R.id.button_create_account).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.button_login) {
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextPassword.getEditableText().toString();
            // TODO
            if(TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(username) || (password.trim().length() < 8))) {
                Toast.makeText(v.getContext(), R.string.error_user_login, Toast.LENGTH_LONG).show();
            } else {
                doLogin(username, password);
            }
        } else if(viewId == R.id.button_create_account) {
            // change to account creation
            invokeNavigationChanged(R.layout.frag_registration, null);
        }
    }

    private void doLogin(String username, String password) {
        // TODO

        DoLogin login = new DoLogin();
        login.execute(username, password);
    }
    private void doLogin2(final String username, final String pwd) {
        final Activity activity = getActivity();

    }

    private void checkLoginButtonStatus(final String username, final String password) {
        boolean enable = false;
        enable = ((!TextUtils.isEmpty(username))
                && (!TextUtils.isEmpty(password)));
        if(mButtonLogin != null) {
            mButtonLogin.setEnabled(enable);
        }
    }

    private class DoLogin extends AsyncTask<String, Void, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String hashedPassword = PasswordUtil.getHashedPassword(password);

            LoginRequest request =
                    new LoginRequest(username,hashedPassword,1, Constants.API_KEY, Constants.deviceUnique);
            LoginResponse response = EcareZoneWebService.api.login(request);
            LoginInfo.userName = username;
            LoginInfo.hashedPassword = hashedPassword;

            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            if(response.status.code == 200){
                final Activity activity = getActivity();
                if(activity != null) {
                    // record the app stauts as "is_login" then the next launch will go to main page directly instead of go to registration page
                    SharedPreferences perPreferences = activity.getSharedPreferences("eCareZone", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = perPreferences.edit();
                    editor.putBoolean("is_login", true);
                    editor.commit();

                    activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                    activity.finish();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Failed to login: "+response.status.message, Toast.LENGTH_LONG).show();
            }
        }
    }

}
