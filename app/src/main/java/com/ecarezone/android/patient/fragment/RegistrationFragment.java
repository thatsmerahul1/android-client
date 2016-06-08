package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.RegistrationDialogFragment;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.model.database.UserTable;
import com.ecarezone.android.patient.model.rest.Data;
import com.ecarezone.android.patient.model.rest.LoginRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.model.rest.SignupRequest;
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
 * Created by CHAO WEI on 5/1/2015.
 */
public class RegistrationFragment extends EcareZoneBaseFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, SinchService.StartFailedListener {

    private ProgressDialog progressDialog;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    private static final String TAG = RegistrationFragment.class.getSimpleName();
    private EditText mSpinnerCountry = null;
    private EditText mSpinnerLanguage = null;
    private View mButtonRegister = null;
    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private CheckBox mCheckBoxTerms = null;
    public static final Integer COUNTRY_RESULT = 100;
    public static final Integer LANGUAGE_RESULT = 101;
    private String hashedPassword;
    private UserTable userTable;
    private TextView mStartService;
    private LocationFinder locationFinder;
    int flag = 0;


    @Override
    protected String getCallerName() {
        return RegistrationFragment.class.getSimpleName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_registration, container, false);
        mButtonRegister = view.findViewById(R.id.button_register);
        mButtonRegister.setOnClickListener(this);
        mButtonRegister.setEnabled(false);
        mEditTextUsername = (EditText) view.findViewById(R.id.edit_text_registration_username);
        mEditTextUsername.setCompoundDrawables(null,null,null,null);
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkRegistrationButtonStatus();
                } catch (Exception e) {
                    EcareZoneLog.e(getCallerName(), e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEditTextPassword = (EditText) view.findViewById(R.id.edit_text_registration_password);
        mEditTextPassword.setCompoundDrawables(null,null,null,null);
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    checkRegistrationButtonStatus();
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

        mSpinnerCountry = (EditText) view.findViewById(R.id.country_spinner);
        mSpinnerLanguage = (EditText) view.findViewById(R.id.spinner_language);
        mSpinnerCountry.setKeyListener(null);
        mSpinnerCountry.setOnClickListener(this);
        mSpinnerLanguage.setKeyListener(null);
        //English selected as a default language
        mSpinnerLanguage.setText(R.string.language_english);
        mSpinnerLanguage.setTag(getResources().getString(R.string.language_local_english));
        mSpinnerLanguage.setOnClickListener(this);
        mCheckBoxTerms = (CheckBox) view.findViewById(R.id.checkbox_registration_terms);
        mCheckBoxTerms.setOnCheckedChangeListener(this);

        String txt = "<HTML>"+getString(R.string.registration_agreement_you_agree)+
                " <b><a href=\"http://ecarezone.com\">"+getString(R.string.terms)+"</a></b> " +getString(R.string.and)+"<b>" +
                " <a href=\"http://ecarezone.com\">"+getString(R.string.privacy_policy)+"</a></b></HTML>";

        mCheckBoxTerms.setText(Html.fromHtml(txt));
        mCheckBoxTerms.setMovementMethod(LinkMovementMethod.getInstance());


        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == null) return;

        Util.hideKeyboard(getActivity());

        final int viewId = v.getId();
        if (viewId == R.id.button_register) {
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextPassword.getEditableText().toString();
            //Client side validation for username and password
            /*if (TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(password) || (password.trim().length() < 8))) {
                Toast.makeText(v.getContext(), R.string.error_password_less_than_registration, Toast.LENGTH_LONG).show();
            } else {
                mButtonRegister.setEnabled(false);
                doRegistration(username, password, (String) mSpinnerCountry.getTag(), (String) mSpinnerLanguage.getTag());
            }*/
            if (TextUtils.isEmpty(username)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.trim()).matches())
                    || (TextUtils.isEmpty(password) || (password.trim().length() < 8))) {
                Toast.makeText(v.getContext(), R.string.error_password_less_than_registration, Toast.LENGTH_LONG).show();
            } else {
                mButtonRegister.setEnabled(false);
                doRegistration(username, password, (String) mSpinnerCountry.getTag(), (String) mSpinnerLanguage.getTag());
            }
        } else if (viewId == R.id.country_spinner) {
            createRegestrationDialog(Constants.COUNTRY);
        } else if (viewId == R.id.spinner_language) {
            createRegestrationDialog(Constants.LANGUAGE);
        }
    }

    private void createRegestrationDialog(String type) {
        RegistrationDialogFragment regFragment = new RegistrationDialogFragment();
        FragmentManager fm = getFragmentManager();
        Bundle bun = new Bundle();
        bun.putString(Constants.TYPE, type);
        if (type.equalsIgnoreCase(Constants.COUNTRY)) {
            regFragment.setTargetFragment(this, COUNTRY_RESULT);
        } else {
            regFragment.setTargetFragment(this, LANGUAGE_RESULT);
        }
        regFragment.setArguments(bun);

        regFragment.show(fm, Constants.REGISTRATION_DIALOG_TAG);

    }

    /*
          Result from dialog fragment for country and language selection
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COUNTRY_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();
                String country = bundle.getString(Constants.ITEM, null);
                String countryCode = bundle.getString(Constants.ITEM_CODE, null);
                if (country != null) {
                    mSpinnerCountry.setText(country);
                    mSpinnerCountry.setTag(countryCode);
                    checkRegistrationButtonStatus();
                }

            }
        } else if (requestCode == LANGUAGE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();
                String language = bundle.getString(Constants.ITEM, null);
                String languageCode = bundle.getString(Constants.ITEM_CODE, null);
                if (language != null) {
                    mSpinnerLanguage.setText(language);
                    mSpinnerLanguage.setTag(languageCode);
                    checkRegistrationButtonStatus();
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == null) {
            return;
        }

        final int viewId = buttonView.getId();

        if (viewId == R.id.checkbox_registration_terms) {
            try {
                checkRegistrationButtonStatus();
            } catch (Exception e) {
                EcareZoneLog.e(getCallerName(), e);
            }
        }
    }

    //Enabling the create account button once all fields entered
    private void checkRegistrationButtonStatus() {
        boolean enable = false;
        boolean isChecked = mCheckBoxTerms.isChecked();
        String username = mEditTextUsername.getText().toString();
        String password = mEditTextPassword.getText().toString();
        String country = mSpinnerCountry.getText().toString();
        String language = mSpinnerLanguage.getText().toString();
        enable = ((!TextUtils.isEmpty(username))
                && (!TextUtils.isEmpty(password))
                && isChecked
                && (!TextUtils.isEmpty(country))
                && (!TextUtils.isEmpty(language)));
        if (mButtonRegister != null) {
            mButtonRegister.setEnabled(enable);
        }
    }

    //Create account request
    private void doRegistration(String username, String password, String country, String language) {
        //TODO register
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), getText(R.string.progress_dialog_create_account).toString());
        hashedPassword = PasswordUtil.getHashedPassword(password);
        SignupRequest signupRequest = new SignupRequest(username, hashedPassword, 1,
                country, language, "N/A", "N/A", Constants.API_KEY, Constants.deviceUnique);
        getSpiceManager().execute(signupRequest, new DosSettingsRequestListener());


    }

    private void doLogin(final String username, final String password) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.registration_successful_dialog);
        mStartService = (TextView)dialog.findViewById(R.id.start_service);
        mStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationFinder = new LocationFinder(getActivity());
                hashedPassword = PasswordUtil.getHashedPassword(password);
                LoginRequest request =
                        new LoginRequest(username, hashedPassword, 1, Constants.API_KEY, Constants.deviceUnique, locationFinder.getLatitude(), locationFinder.getLongitude());
                final LoginResponse response = new LoginResponse();
                progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Logging ........");
                getSpiceManager().execute(request, new LoginRequestListener());
                dialog.dismiss();

            }
        });
        dialog.show();
        progressDialog.dismiss();
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
                LoginInfo.role = String.valueOf(0);

                if (activity != null) {
                    // record the app stauts as "is_login" then the next launch will go to main page directly instead of go to registration page

                    // Make server call & get the user information & save it internally in db.
                    if (data.userProfiles != null) {
                        ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
                        profileDbApi.deleteProfiles(LoginInfo.userId.toString());
                        profileDbApi.saveMultipleProfiles(LoginInfo.userId.toString(), response.data.userProfiles);
                    }
                    userTable = new UserTable(getActivity());
                    /*
                         If user already exists , Updating the data into database.
                         If user doesn't exist , Inserting data in database
                     */
                    if (userTable.userExists(Long.toString(data.userId))) {
                        userTable.updateUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(0), data.settings.country);
                    } else {
                        userTable.saveUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(0), data.settings.country);
                    }
                    /*
                       Saving UserId and Login status into shared preference
                     */
                    SharedPreferences perPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = perPreferences.edit();
                    editor.putBoolean(Constants.IS_LOGIN, true);
                    editor.putString(Constants.USER_ID, String.valueOf(LoginInfo.userId));
                    editor.commit();

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
                progressDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), response.status.message, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
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
        activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
        activity.finish();
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Create account response
    public final class DosSettingsRequestListener implements RequestListener<LoginResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();

        }

        @Override
        public void onRequestSuccess(final LoginResponse loginResponse) {

            if (loginResponse.status.code == 201) {
                Data data = loginResponse.data;
                LoginInfo.userName = data.settings.email;
                LoginInfo.userId = data.userId;
                LoginInfo.hashedPassword = hashedPassword;
                LoginInfo.role = String.valueOf(data.role);

                final Activity activity = getActivity();
                if (activity != null) {
                    userTable = new UserTable(getActivity());
                    /*
                         If user already exists , Updating the data into database.
                         If user doesn't exist , Inserting data in database
                     */
                    if (userTable.userExists(Long.toString(data.userId))) {
                        userTable.updateUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(1), data.settings.country);
                    } else {
                        userTable.saveUserData(Long.toString(data.userId), data.settings.email, hashedPassword, data.settings.language
                                , Integer.toString(1), data.settings.country);
                    }
                    String username = LoginInfo.userName;
                    String password = LoginInfo.hashedPassword;
                    doLogin(username, password);

//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!SinchUtil.getSinchServiceInterface().isStarted()) {
//                                SinchUtil.getSinchServiceInterface().startClient(LoginInfo.userName);
//                            }
//                            clearFields();
//                            invokeNavigationChanged(R.layout.frag_login, null);
//
//                        }
//                    });
                }
            }else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to signup: " + loginResponse.status.message, Toast.LENGTH_LONG).show();
                        flag = 1;
                    }
                });
            }
            progressDialog.dismiss();
        }
    }

    private void clearFields() {
        mEditTextPassword.setText("");
        mEditTextUsername.setText("");
        mSpinnerCountry.setText("");
        mSpinnerLanguage.setText("");
        //English selected as a default language
        mSpinnerLanguage.setText(R.string.language_english);
        mSpinnerLanguage.setTag(getResources().getString(R.string.language_local_english));


    }

}
