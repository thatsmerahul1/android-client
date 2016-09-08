package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.NetworkCheck;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.model.database.UserTable;
import com.ecarezone.android.patient.model.rest.Data;
import com.ecarezone.android.patient.model.rest.LoginRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.service.LocationFinder;
import com.ecarezone.android.patient.service.SinchService;
import com.ecarezone.android.patient.utils.EcareZoneLog;
import com.ecarezone.android.patient.utils.PasswordUtil;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.sinch.android.rtc.SinchError;

/**
 * Created by L&T Technology Services  on 2/19/2016.
 */
public class LoginFragment extends EcareZoneBaseFragment implements View.OnClickListener,
        SinchService.StartFailedListener {
    private ProgressDialog progressDialog;
    private static String TAG = LoginFragment.class.getSimpleName();

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private TextView mTextViewForgotPwd = null;
    private View mButtonLogin = null;
    private UserTable userTable;
    private LocationFinder locationFinder;
    private String hashedPassword;
    private TextView textView_error;
    int i = 1;

    @Override
    protected String getCallerName() {
        return LoginFragment.class.getSimpleName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_login, container, false);
        locationFinder = new LocationFinder(getActivity());
        mEditTextUsername = (EditText) view.findViewById(R.id.edit_text_login_username);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s != null) {
                        checkLoginButtonStatus(String.valueOf(s),
                                mEditTextPassword.getEditableText().toString());
                        mEditTextUsername.getBackground().clearColorFilter();
                    }
                } catch (Exception e) {
                    EcareZoneLog.e(getCallerName(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        mEditTextPassword = (EditText) view.findViewById(R.id.edit_text_login_password);
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
        //if User already exists
        mEditTextUsername.setText(LoginInfo.userName);

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
        mTextViewForgotPwd = (TextView) view.findViewById(R.id.textview_forgotpwd);
        if (LoginInfo.userName != null) {
            mEditTextUsername.setText(LoginInfo.userName);
        }
        mTextViewForgotPwd.setOnClickListener(this);
        mTextViewForgotPwd.setEnabled(true);

        mButtonLogin = view.findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        view.findViewById(R.id.button_create_account).setOnClickListener(this);
        textView_error = (TextView) view.findViewById(R.id.textview_error);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        Util.hideKeyboard(getActivity());

        final int viewId = v.getId();
        if (viewId == R.id.button_login) {
            textView_error.setVisibility(View.INVISIBLE);
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextPassword.getEditableText().toString();
            /*
                Checking client side validation during login
            */
            if (TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(password) || (password.trim().length() < 8))) {
                textView_error.setText(R.string.error_user_login);
                textView_error.setVisibility(View.VISIBLE);
                mEditTextPassword.setText("");
                Toast.makeText(v.getContext(), R.string.error_user_login, Toast.LENGTH_LONG).show();
                return;
            } else {
                if(NetworkCheck.isNetworkAvailable(getActivity())) {
                    doLogin(username, password);
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }

        } else if (viewId == R.id.button_create_account) {
            // change to account creation
            invokeNavigationChanged(R.layout.frag_registration, null);
        } else if (viewId == R.id.textview_forgotpwd) {
//            if (LoginInfo.userName == null) {
//                textView_error.setText(R.string.error_user_login);
//                textView_error.setVisibility(View.VISIBLE);
//            } else {
            Bundle bndl = new Bundle();
            bndl.putString("email", mEditTextUsername.getText().toString());
            invokeNavigationChanged(R.layout.act_forgotpassword, bndl);
//            }
        }
    }

    //Login Request
    private void doLogin(String username, String password) {
        // Changing password to hashed password for security

        hashedPassword = PasswordUtil.getHashedPassword(password);
        LoginRequest request =
                new LoginRequest(username, hashedPassword, 1, Constants.API_KEY, Constants.deviceUnique, locationFinder.getLatitude(), locationFinder.getLongitude());
        final LoginResponse response = new LoginResponse();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Logging in........");
        getSpiceManager().execute(request, new LoginRequestListener());

    }


    /*
         Login Button is enable only all fields entered
     */
    private void checkLoginButtonStatus(final String username, final String password) {
        boolean enable = false;
        enable = ((!TextUtils.isEmpty(username))
                && (!TextUtils.isEmpty(password)));
        if (mButtonLogin != null) {
            mButtonLogin.setEnabled(enable);
        }
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }

    //Login response listner
    public final class LoginRequestListener implements RequestListener<LoginResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "NetWork Failure");
            Toast.makeText(getActivity(), "failure", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            mEditTextPassword.setText("");

        }

        @Override
        public void onRequestSuccess(final LoginResponse response) {
            mEditTextPassword.setText("");
            Log.d(TAG, "Status Code " + response.status.code);
            if (response.status.code == 200) {
                final Activity activity = getActivity();
                Data data = response.data;
                LoginInfo.userName = data.settings.email;
                LoginInfo.userId = data.userId;
                LoginInfo.hashedPassword = hashedPassword;
                LoginInfo.role = String.valueOf(1);
                LoginInfo.recommandedDoctorId = data.recommandedDoctorId;
                LoginInfo.isShown = false;


                if (activity != null) {
                    Log.d(TAG, "Profiles Size" + response.data.userProfiles.length +
                            "Email" + response.data.settings.email + "Avilabilty Status" + response.data.status);
                    // record the app stauts as "is_login" then the next launch will go to main page directly instead of go to registration page

                    // Make server call & get the user information & save it internally in db.

                    ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                    profileDbApi.deleteProfiles(LoginInfo.userId.toString());
                    profileDbApi.saveMultipleProfiles(LoginInfo.userId.toString(), response.data.userProfiles);

                    userTable = new UserTable(getActivity());
                    /*
                         If user already exists , Updating the data into database.
                         If user doesn't exist , Inserting data in database
                     */
                    if (userTable.userExists(Long.toString(data.userId))) {
                        userTable.updateUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(1), data.settings.country, data.recommandedDoctorId);
                    } else {
                        userTable.saveUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(1), data.settings.country, data.recommandedDoctorId);
                    }
                    /*
                       Saving UserId and Login status into shared preference
                     */
                    SharedPreferences perPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = perPreferences.edit();
                    editor.putBoolean(Constants.IS_LOGIN, true);
                    editor.putString(Constants.USER_ID, String.valueOf(LoginInfo.userId));
                    editor.commit();

                    SharedPreferences languagePreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor langEditor = languagePreferences.edit();
                    langEditor.putString(Constants.LANGUAGE, data.settings.language);
                    langEditor.commit();
                    SharedPreferences countryPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor countryEditor = countryPreferences.edit();
                    countryEditor.putString(Constants.COUNTRY, data.settings.language);
                    countryEditor.commit();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!SinchUtil.getSinchServiceInterface().isStarted()) {
                                SinchUtil.getSinchServiceInterface().startClient(LoginInfo.userName);
                            }
                            nextScreen(activity);
                        }
                    });

                }
            } else {
                textView_error.setText(response.status.message);
                textView_error.setVisibility(View.VISIBLE);

                int color = getResources().getColor(android.R.color.holo_red_light);
                mEditTextUsername.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                mEditTextPassword.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            Log.d(TAG, "Login Success");
        }
    }


    private void nextScreen(Activity activity) {

        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        intent.putExtra("from_login_screen", true);
        activity.startActivity(intent);
        activity.finish();
    }

}
