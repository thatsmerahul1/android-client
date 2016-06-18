package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.AboutEcareZoneActivity;
import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.NetworkCheck;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.UpdatePasswordActivity;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.RegistrationDialogFragment;
import com.ecarezone.android.patient.model.User;
import com.ecarezone.android.patient.model.database.UserTable;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.model.rest.SettingsRequest;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class SettingsFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private ImageView registrationImage;
    private TextView titleTextview;
    private EditText mSpinnerCountry = null;
    private EditText mSpinnerLanguage = null;
    private View mButtonRegister = null;
    private EditText mEditTextUsername = null;
    private EditText mEditTextPassword = null;
    private CheckBox mCheckBoxTerms = null;
    private TextView mTextViewAbout = null;
    private TextView ecare_account = null;
    private TextView ecare_account_email = null;
    public static final Integer COUNTRY_RESULT = 100;
    public static final Integer LANGUAGE_RESULT = 101;
    private User user;
    ImageView arrow_down_for_country;
    ImageView arrow_down_for_language;
    private UserTable userTable;

    @Override
    protected String getCallerName() {
        return SettingsFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_registration, container, false);
        registrationImage = (ImageView) view.findViewById(R.id.imageview_registration);
        titleTextview = (TextView) view.findViewById(R.id.textview_title);
        registrationImage.setVisibility(View.GONE);
        titleTextview.setVisibility(View.GONE);

        mButtonRegister = view.findViewById(R.id.button_register);
        mButtonRegister.setOnClickListener(this);
        mButtonRegister.setVisibility(View.GONE);
        arrow_down_for_country = (ImageView)view.findViewById(R.id.arrow_down);
        arrow_down_for_language = (ImageView)view.findViewById(R.id.arrow_down2);
        arrow_down_for_country.setImageResource(R.drawable.black_arrow);
        arrow_down_for_language.setImageResource(R.drawable.black_arrow);
        mEditTextUsername = (EditText) view.findViewById(R.id.edit_text_registration_username);
        mEditTextUsername.setEnabled(false);
        mEditTextPassword = (EditText) view.findViewById(R.id.edit_text_registration_password);
        mEditTextPassword.setOnClickListener(this);
        mEditTextPassword.setKeyListener(null);
        mEditTextPassword.setFocusable(false);
        //Disabling the username and password fields
        mEditTextUsername.setEnabled(false);
        // add editor action listener for password EditText to accept input from soft keyboard
        // Then the user need not to click login button
        mSpinnerCountry = (EditText) view.findViewById(R.id.country_spinner);
        mSpinnerLanguage = (EditText) view.findViewById(R.id.spinner_language);
        mSpinnerCountry.setKeyListener(null);
        mSpinnerCountry.setOnClickListener(this);
        mSpinnerLanguage.setKeyListener(null);
        mSpinnerLanguage.setText(R.string.language_english);
        mSpinnerLanguage.setTag("en");
        mSpinnerLanguage.setOnClickListener(this);
        mCheckBoxTerms = (CheckBox) view.findViewById(R.id.checkbox_registration_terms);
        mCheckBoxTerms.setVisibility(View.GONE);
        mTextViewAbout = (TextView) view.findViewById(R.id.textview_registration_about);
        mTextViewAbout.setVisibility(View.VISIBLE);
        mTextViewAbout.setOnClickListener(this);
        ecare_account = (TextView)view.findViewById(R.id.ecare_account);
        ecare_account_email = (TextView)view.findViewById(R.id.ecare_account_email);
        ecare_account.setVisibility(View.VISIBLE);
        ecare_account_email.setVisibility(View.VISIBLE);
        ecare_account_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Customersupport@Ecarezone.com"});
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(emailIntent);
            }
        });
        updateUserData();
        //Enabling the option menu use
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.main_side_menu_settings));

        return view;
    }

    //Updating UI with existing user data
    private void updateUserData() {
        List<String> countryCodeArray = Arrays.asList(getResources().getStringArray(R.array.country_code_array));
        String[] countryArray = getResources().getStringArray(R.array.country_array);
        List<String> languageCodeArray = Arrays.asList(getResources().getStringArray(R.array.language_local_array));
        String[] languageArray = getResources().getStringArray(R.array.language_array);
        user = userTable.getUserData(LoginInfo.userId.toString());
        if (user != null) {
            mEditTextUsername.setText(user.email);
            mEditTextPassword.setText(user.password);
            mSpinnerCountry.setText(countryArray[countryCodeArray.indexOf(user.country)]);
            mSpinnerCountry.setTag(user.country);
            mSpinnerLanguage.setText(languageArray[languageCodeArray.indexOf(user.language)]);
            mSpinnerLanguage.setTag(user.language);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing the user table to access the user data
        userTable = new UserTable(getActivity());
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        final int viewId = v.getId();
        if (viewId == R.id.country_spinner) {
            createRegestrationDialog(Constants.COUNTRY);
        } else if (viewId == R.id.button_register) {

        } else if (viewId == R.id.spinner_language) {
            createRegestrationDialog(Constants.LANGUAGE);
        } else if (viewId == R.id.textview_registration_about) {
            if(NetworkCheck.isNetworkAvailable(getActivity())){
                getActivity().startActivity(new Intent(getActivity(), AboutEcareZoneActivity.class));
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            }
        } else if(viewId == R.id.edit_text_registration_password)
        {
            Log.d("Naga","Updating Password ");
            getActivity().startActivity(new Intent(getActivity(), UpdatePasswordActivity.class));
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
                    user.country = countryCode;
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
                    user.language = languageCode;
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_check, menu);
        MenuItem checkItem = menu.findItem(R.id.action_check);
        checkItem.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_check) {
            final String username = mEditTextUsername.getEditableText().toString();
            final String password = mEditTextPassword.getEditableText().toString();

            if(NetworkCheck.isNetworkAvailable(getActivity())) {
                doSettingsUpdate(username, password, (String) mSpinnerCountry.getTag(), (String) mSpinnerLanguage.getTag());
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Updating setting request
    private void doSettingsUpdate(String username, String password, String country, String language) {
        //TODO register
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), getText(R.string.progress_dialog_save).toString());

        SettingsRequest settingsRequest = new SettingsRequest(username, password
                , 1, country, language, "N/A", "N/A", Constants.API_KEY, Constants.deviceUnique);
        getSpiceManager().execute(settingsRequest, new DosSettingsRequestListener());

    }

    //Updating setting response
    public final class DosSettingsRequestListener implements RequestListener<LoginResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();

        }

        @Override
        public void onRequestSuccess(final LoginResponse loginResponse) {
            //updating the latest user data in to usertable
            userTable = new UserTable(getActivity());
            userTable.updateUserData(user.userId, user.email, mEditTextPassword.getEditableText().toString(), user.language
                    , Integer.toString(1), user.country,user.recommandedDoctorId );
            if (loginResponse.status.code == 200) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), loginResponse.status.message, Toast.LENGTH_LONG).show();
                    }
                });
            }
            progressDialog.dismiss();
        }
    }
}



