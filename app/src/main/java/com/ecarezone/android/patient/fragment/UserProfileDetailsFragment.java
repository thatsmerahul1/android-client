package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.model.rest.CreateProfileRequest;
import com.ecarezone.android.patient.model.rest.CreateProfileResponse;
import com.ecarezone.android.patient.model.rest.DeleteProfileRequest;
import com.ecarezone.android.patient.model.rest.UpdateProfileRequest;
import com.ecarezone.android.patient.model.rest.UploadImageResponse;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.utils.EcareZoneLog;
import com.ecarezone.android.patient.utils.ImageUtil;
import com.ecarezone.android.patient.utils.MD5Util;
import com.ecarezone.android.patient.utils.PermissionUtil;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class UserProfileDetailsFragment extends EcareZoneBaseFragment implements
        View.OnClickListener, TextWatcher, Toolbar.OnMenuItemClickListener {

    private EditText profileNameET;
    private Activity mActivity = null;
    private Toolbar mToolBar = null;
    private UserProfile mProfile = null;
    private View view = null;
    private static final int REQUEST_SELECT_PICTURE = 1;

    private ImageView profileImageButton;
    private String mSelectedPhotoPath = null;
    private String mUploadedImageUrl = null;
    private ProgressDialog mProgressDialog;

    private EditText mHeightEditText;
    private EditText mWeightEditText;
    private TextView mErrorText;

    @Override
    protected String getCallerName() {
        return UserProfileDetailsFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_user_profle_details, container, false);
        ((ProfileDetailsActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.profile_actionbar_details_title));
        mSelectedPhotoPath = null;
        mUploadedImageUrl = null;
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mToolBar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            mToolBar.setOnMenuItemClickListener(this);
        }

        /* Whenever profile name is changed, check the name length to enable or disable the save button in action bar. */
        profileNameET = (EditText) view.findViewById(R.id.profileName);
        profileNameET.addTextChangedListener(this);

        EditText nameET = (EditText) view.findViewById(R.id.name);
        nameET.addTextChangedListener(this);

        mHeightEditText = (EditText) view.findViewById(R.id.height);
        mWeightEditText = (EditText) view.findViewById(R.id.weight);
        mErrorText = (TextView) view.findViewById(R.id.txtErrorMsg);

        ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());

        String myProfileText = getResources().getString(R.string.profile_mine);
        if (!mActivity.getIntent().getBooleanExtra(ProfileDetailsActivity.IS_NEW_PROFILE, false)) {
            // Profile exists. Retrieve from DB and display the profile details
            String profileId = mActivity.getIntent().getStringExtra(ProfileDetailsActivity.PROFILE_ID);
            if (profileId != null) {
                mProfile = profileDbApi.getProfile(LoginInfo.userId.toString(), profileId);
            }
        } else if (!profileDbApi.hasProfile(LoginInfo.userId.toString())) {
            // No profiles found. make this as "My profile"
            profileNameET.setText(myProfileText);
            profileNameET.setEnabled(false);
        }

        Button deleteProfileBtn = (Button) view.findViewById(R.id.deleteProfileBtn);

        if (mProfile != null) {
            setProfileValuesToFormFields(mProfile);
            deleteProfileBtn.setOnClickListener(this);
            if (mProfile.profileName != null && mProfile.profileName.equals(myProfileText)) {
                // For "My profile" disable delete button & the profile name field
                profileNameET.setEnabled(false);
                deleteProfileBtn.setEnabled(false);
            }
        } else {
            // This is creating new profile. So, disabling the delete profile button
            deleteProfileBtn.setEnabled(false);
        }

        view.findViewById(R.id.dob).setOnClickListener(this);
        view.findViewById(R.id.gender).setOnClickListener(this);

        profileImageButton = (ImageView) view.findViewById(R.id.imageButton);
        profileImageButton.setOnClickListener(this);

        // Get the image url. Check if profile has an url else get a Gravatar
        String imageUrl = null;
        if (mProfile != null) {
            if (mProfile.avatarUrl == null || mProfile.avatarUrl.equals("")) {
                //size of the profile picture to download from Gravatar. Giving the dimensions of the image container(imageView)
                int imageSize = getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
                if (mProfile.email != null) {
                    // convert the email string to md5hex and pass it in the Gravatarl url.
                    String hashEmail = MD5Util.md5Hex(mProfile.email);
                    imageUrl = Constants.GRAVATOR_URL + hashEmail + "?d=" + Constants.DEFAULT_GRAVATOR_IMAGE_URL + "?s=" + imageSize;
                } else {
                    imageUrl = null;
                }
            } else {
                imageUrl = mProfile.avatarUrl;
            }
        }
        // If imageUrl is available load it via Picasso
        if (imageUrl != null && imageUrl.trim().length() > 8) {
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(profileImageButton);
        } else {
            // When no avatarUrl found, use local default image.
            profileImageButton.setImageResource(R.drawable.news_other);
        }

        try {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            ;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_check) {

            Util.hideKeyboard(getActivity());

            boolean isError = mErrorText.getText().length() > 0 ? true : false;
            mErrorText.setText("");

            String height = mHeightEditText.getText().toString();
            if (height.trim().length() > 1) {
                try {
                    double heightVal = Double.parseDouble(height);
                    if (!(heightVal < 243.84 && heightVal > 30)) {
                        mErrorText.setText(getString(R.string.invalid_height));
                        return true;
                    }
                } catch (NumberFormatException nfe) {

                }
            }

            String weight = mWeightEditText.getText().toString();
            if (weight.trim().length() > 1) {
                try {
                    double weightVal = Double.parseDouble(weight);
                    if (!(weightVal < 300 && weightVal > 1)) {
                        mErrorText.setText(getString(R.string.invalid_weight));
                        return true;
                    }
                } catch (NumberFormatException nfe) {

                }
            }

            if (!isError) {
                saveProfile();
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageUtil.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // sets to the image view
            setPic(mSelectedPhotoPath);
        } else if (requestCode == REQUEST_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // copy the selected image to temporary file and get the image path
                mSelectedPhotoPath = ImageUtil.saveUriPhotoToFile(data.getData(), getActivity());
                setPic(mSelectedPhotoPath);
            }
        }
    }

    /* scales & sets the image thumbnail to the profile image button*/
    private void setPic(String imagePath) {
            Bitmap bitmap = ImageUtil.createScaledBitmap(imagePath, profileImageButton.getWidth(), profileImageButton.getHeight());
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            boolean doApplyMatrix = false;
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    doApplyMatrix = true;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    doApplyMatrix = true;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    doApplyMatrix = true;
                    break;
                default:
                    break;
            }

            if (doApplyMatrix) {
                Bitmap imgBitmap = Bitmap.createBitmap(bitmap, 0,
                        0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);
                saveBitmapToFile(imagePath, imgBitmap);


                profileImageButton.setImageBitmap(imgBitmap);
            } else {
                profileImageButton.setImageBitmap(bitmap);
            }
        }

    public static boolean saveBitmapToFile(String file, Bitmap bmp) {

        FileOutputStream out = null;
        boolean isSavingSuccessful = false;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            isSavingSuccessful = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSavingSuccessful;
    }
    /* sets the provide profile details in the UI fields */
    private void setProfileValuesToFormFields(UserProfile profile) {
        ((EditText) view.findViewById(R.id.name)).setText(profile.name);
        ((EditText) view.findViewById(R.id.gender)).setText(profile.gender);
        ((EditText) view.findViewById(R.id.address)).setText(profile.address);
        ((EditText) view.findViewById(R.id.dob)).setText(profile.birthdate);
        ((EditText) view.findViewById(R.id.ethnicity)).setText(profile.ethnicity);
        ((EditText) view.findViewById(R.id.profileName)).setText(profile.profileName);
        mHeightEditText.setText(profile.height);
        mWeightEditText.setText(profile.weight);
    }

    private void setDateToDobField(String date) {
        ((EditText) view.findViewById(R.id.dob)).setText(date);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mProfile != null) {
            // Profile has already information. enabling the save button in action bar menu
            MenuItem saveMenuItem = mToolBar.getMenu().findItem(R.id.action_check);
            saveMenuItem.setEnabled(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void deleteProfile() {
        long profileId = Long.parseLong(mProfile.profileId);
        mProgressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getResources().getString(R.string.progress_dialog_delete));
        DeleteProfileRequest request = new DeleteProfileRequest(profileId);
        getSpiceManager().execute(request, "profile_delete", DurationInMillis.ALWAYS_EXPIRED, new DeleteProfileResponseListener());
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    // read all the fields in the UI and save the profile
    private void saveProfile() {
        final UserProfile userProfile = new UserProfile();
        userProfile.name = ((EditText) view.findViewById(R.id.name)).getText().toString();
        userProfile.gender = ((EditText) view.findViewById(R.id.gender)).getText().toString();
        userProfile.address = ((EditText) view.findViewById(R.id.address)).getText().toString();
        userProfile.birthdate = ((EditText) view.findViewById(R.id.dob)).getText().toString();
        userProfile.profileName = ((EditText) view.findViewById(R.id.profileName)).getText().toString();
        userProfile.email = LoginInfo.userName;
        userProfile.ethnicity = ((EditText) view.findViewById(R.id.ethnicity)).getText().toString();
        userProfile.height = ((EditText) view.findViewById(R.id.height)).getText().toString();
        userProfile.weight = ((EditText) view.findViewById(R.id.weight)).getText().toString();

        if ((TextUtils.isEmpty(((EditText) view.findViewById(R.id.name)).getText().toString())) ||
                (TextUtils.isEmpty(((EditText) view.findViewById(R.id.gender)).getText().toString())) ||
                (TextUtils.isEmpty(((EditText) view.findViewById(R.id.dob)).getText().toString())) ||
                (TextUtils.isEmpty(((EditText) view.findViewById(R.id.ethnicity)).getText().toString())) ||
                (TextUtils.isEmpty(((EditText) view.findViewById(R.id.height)).getText().toString())) ||
                (TextUtils.isEmpty(((EditText) view.findViewById(R.id.weight)).getText().toString()))) {

            Toast.makeText(mActivity, "Please enter all the fields", Toast.LENGTH_LONG).show();

        } else {
            SaveProfileAsyncTask saveProfileAsyncTask = new SaveProfileAsyncTask();
            saveProfileAsyncTask.execute(userProfile);
        }

    }

    class SaveProfileAsyncTask extends AsyncTask<UserProfile, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                    getResources().getString(R.string.progress_dialog_save));
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(UserProfile... params) {
            UserProfile userProfile = params[0];
            // if image is selected, upload it and receive the image URL.
            UploadImageResponse uploadImageResponse = null;
            if (mSelectedPhotoPath != null) {
                uploadImageResponse = ImageUtil.uploadProfilePicture(mSelectedPhotoPath);
            }
            if (uploadImageResponse != null) {
                mUploadedImageUrl = uploadImageResponse.data.avatarUrl;
            }

            if (mUploadedImageUrl != null) {
                // user changed the current image
                userProfile.avatarUrl = mUploadedImageUrl;
            } else if (mProfile != null && mProfile.avatarUrl != null) {
                // user not changed the current image
                userProfile.avatarUrl = mProfile.avatarUrl;
            } else {
                userProfile.avatarUrl = null;
            }

            if (mProfile == null) {
                // Create new profile & save in local DB
                saveProfileInServer(userProfile);
            } else {
                // Update the current profile in server and in local DB
                long profileId = Long.parseLong(mProfile.profileId);
                updateProfileInServer(profileId, userProfile);
            }
            return null;
        }
    }

    /* creates a new profile in server by calling web service api */
    private void saveProfileInServer(UserProfile userProfile) {
        CreateProfileRequest request = new CreateProfileRequest();
        request.userProfile = userProfile;
        getSpiceManager().execute(request, "profile_create", DurationInMillis.ALWAYS_EXPIRED, new CreateProfileResponseListener());
    }

    /* updates the current profile in server */
    private void updateProfileInServer(long profileId, final UserProfile userProfile) {
        UpdateProfileRequest request = new UpdateProfileRequest(profileId);
        request.userProfile = userProfile;
        getSpiceManager().execute(request, "profile_update", DurationInMillis.ALWAYS_EXPIRED, new UpdateProfileResponseListener());
    }

    /* creates and fires an intent that opens gallery to pick a photo */
    private void dispatchSelectFromGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.gallery_chooser_select_picture))
                , REQUEST_SELECT_PICTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton:
                showPhotoSelectOptionsDialog();
                break;
            case R.id.dob:
                DialogFragment newFragment = new DatePickerFragment();
                String dateStr = ((EditText) view.findViewById(R.id.dob)).getText().toString();
                ((DatePickerFragment) newFragment).setDate(dateStr);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.deleteProfileBtn:
                deleteProfile();
                break;
            case R.id.gender:
                showGenderSelectorDialog();
                break;
        }
    }

    private void showPhotoSelectOptionsDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_dialog_for_profile_image);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int dp = getActivity().getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);

        dialog.show();
        dialog.getWindow().setLayout(width-dp,LinearLayout.LayoutParams.WRAP_CONTENT);
        Button takePhoto = (Button) dialog.findViewById(R.id.takePhoto);
        takePhoto.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (PermissionUtil.isPermissionRequired()
                            && PermissionUtil.getAllpermissionRequired(getActivity(),
                            PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS).length > 0) {

                        PermissionUtil.setAllPermission(getActivity(),
                                PermissionUtil.REQUEST_CODE_ASK_CAPTURE_PHOTO_PERMISSIONS,
                                PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS);
                    } else {
                        // already have all permissions

                        mSelectedPhotoPath = ImageUtil.dispatchTakePictureIntent(getActivity(),false, null);
                    }
                dialog.dismiss();
            }

        });

        Button choosePhoto = (Button) dialog.findViewById(R.id.choosePhoto);
        choosePhoto.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.isPermissionRequired()
                        && PermissionUtil.getAllpermissionRequired(getActivity(),
                        PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSIONS).length > 0) {

                    PermissionUtil.setAllPermission(getActivity(),
                            PermissionUtil.REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE_PERMISSIONS,
                            PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSIONS);
                } else {
                    // already have all permissions
                    dispatchSelectFromGalleryIntent();
                }
                dialog.dismiss();
            }
        });


        Button declineButton = (Button) dialog.findViewById(R.id.okButton);
        declineButton.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        /*new AlertDialog.Builder(getActivity())
                .setItems(R.array.photo_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 1) {
                            if (PermissionUtil.isPermissionRequired()
                                    && PermissionUtil.getAllpermissionRequired(getActivity(),
                                    PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS).length > 0) {

                                PermissionUtil.setAllPermission(getActivity(),
                                        PermissionUtil.REQUEST_CODE_ASK_CAPTURE_PHOTO_PERMISSIONS,
                                        PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS);
                            } else {
                                // already have all permissions

                                mSelectedPhotoPath = ImageUtil.dispatchTakePictureIntent(getActivity(),false, null);
                            }
                        } else {
                            if (PermissionUtil.isPermissionRequired()
                                    && PermissionUtil.getAllpermissionRequired(getActivity(),
                                    PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSIONS).length > 0) {

                                PermissionUtil.setAllPermission(getActivity(),
                                        PermissionUtil.REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE_PERMISSIONS,
                                        PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSIONS);
                            } else {
                                // already have all permissions
                                dispatchSelectFromGalleryIntent();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();*/
    }


    private void showGenderSelectorDialog() {
        new AlertDialog.Builder(getActivity())
                .setItems(R.array.gender, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String gender;
                        if (which == 1) {
                            gender = getResources().getStringArray(R.array.gender)[1];
                        } else {
                            gender = getResources().getStringArray(R.array.gender)[0];
                        }
                        ((EditText) view.findViewById(R.id.gender)).setText(gender);
                    }
                })
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            if (s.length() > 2) {
                MenuItem saveMenuItem = (MenuItem) mToolBar.getMenu().findItem(R.id.action_check);
                if (saveMenuItem != null) saveMenuItem.setEnabled(true);
            }
        } catch (Exception e) {
            EcareZoneLog.e(getCallerName(), e);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    // Robospice response listeners
    private class CreateProfileResponseListener implements RequestListener<CreateProfileResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(CreateProfileResponse response) {
            if (response != null && response.profileId != null && Integer.parseInt(response.profileId) > 0) {

                UserProfile profile = createUserProfileFromResponse(response);

                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                profileDbApi.saveProfile(LoginInfo.userId.toString(), profile, response.profileId);

                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().finish();
            }
            dismissDialog();
        }
    }

    private class UpdateProfileResponseListener implements RequestListener<CreateProfileResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(CreateProfileResponse response) {
            if (response != null && response.profileId != null && Integer.parseInt(response.profileId) > 0) {
                UserProfile profile = createUserProfileFromResponse(response);

                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                profileDbApi.updateProfile(LoginInfo.userId.toString(), profile, mProfile.profileId);

                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().finish();
            }
            dismissDialog();
        }
    }

    private class DeleteProfileResponseListener implements RequestListener<BaseResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(BaseResponse response) {
            if (response != null && response.status != null && response.status.code == 200) {
                // Code 200 is for successful deletion of profile.
                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                profileDbApi.deleteProfile(LoginInfo.userId.toString(), mProfile.profileId);
                getActivity().setResult(getActivity().RESULT_OK, null);
                getActivity().finish();
            }
            dismissDialog();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private UserProfile createUserProfileFromResponse(CreateProfileResponse response) {
        if (response != null) {
            UserProfile profile = new UserProfile();
            profile.profileName = response.profileName;
            profile.email = response.email;
            profile.height = response.height;
            profile.name = response.name;
            profile.address = response.address;
            profile.weight = response.weight;
            profile.avatarUrl = response.avatarUrl;
            profile.profileId = response.profileId;
            profile.ethnicity = response.ethnicity;
            profile.gender = response.gender;
            profile.birthdate = response.birthdate;
            return profile;
        } else {
            return null;
        }
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        int[] date = new int[3];

        // instance block to initialize the date value
        {
            date[0] = -1;
        }

        public void setDate(String strDate) {
            try {
                String[] splitDate = strDate.split("-");
                date[0] = Integer.parseInt(splitDate[0]);
                date[1] = Integer.parseInt(splitDate[1]);
                date[2] = Integer.parseInt(splitDate[2]);
            } catch (NumberFormatException nfe) {
                date[0] = -1;
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                date[0] = -1;
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year, month, day;
            if (date[0] >= 0) {
                year = date[0];
                // month uses 0-11 values for calendar, so decrement by one to the month value which is visible to user.
                month = date[1] - 1;
                day = date[2];
            } else {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onOptionsMenuClosed(Menu menu) {
            super.onOptionsMenuClosed(menu);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            mErrorText.setText("");

            Calendar cal = Calendar.getInstance();
            int yearT = cal.get(Calendar.YEAR);
            int monthT = cal.get(Calendar.MONTH);
            int dayT = cal.get(Calendar.DAY_OF_MONTH);

            boolean isDateValid = true;
            if (yearT < year) {
                isDateValid = false;
            } else if (yearT == year && monthT < month) {
                isDateValid = false;
            } else if (yearT == year && monthT <= month && dayT <= day) {
                isDateValid = false;
            }

            if (isDateValid) {
                String monthStr;
                if (month + 1 < 10) {
                    monthStr = "0" + (month + 1);
                } else {
                    monthStr = String.valueOf(month + 1);
                }
                StringBuilder dateSb = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
                setDateToDobField(dateSb.toString());
            } else {
                mErrorText.setText(getString(R.string.invalid_birth_date));
            }


            /*if (mErrorText.getText().length() > 0) {
                View v = getActionBarView();
                if (v != null) {
                    View viewTick = v.findViewById(R.id.action_check);
                    if (viewTick != null) {
                        viewTick.setEnabled(false);
                    }
                }
            }
            else{
                View v = getActionBarView();
                if (v != null) {
                    View viewTick = v.findViewById(R.id.action_check);
                    if (viewTick != null) {
                        viewTick.setEnabled(true);
                    }
                }
            }*/
        }
    }

    private View getActionBarView() {
        Window window = getActivity().getWindow();
        View v = window.getDecorView();
        int resId = getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // if any of the permission is not granted. do not process anything. just return.
        for (int isGranted : grantResults) {
            if (isGranted == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                dispatchSelectFromGalleryIntent();
                break;
            case PermissionUtil.REQUEST_CODE_ASK_CAPTURE_PHOTO_PERMISSIONS:
                mSelectedPhotoPath = ImageUtil.dispatchTakePictureIntent(getActivity(),false, null);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
