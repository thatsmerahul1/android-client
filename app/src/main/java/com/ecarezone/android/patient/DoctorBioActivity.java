package com.ecarezone.android.patient;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.DoctorBioFragment;
import com.ecarezone.android.patient.fragment.dialog.AddDoctorRequestDialog;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.rest.AddDoctorRequest;
import com.ecarezone.android.patient.model.rest.AddDoctorResponse;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.Util;
import com.google.android.gms.internal.bn;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by L&T Technology Services on 22-02-2016.
 */
public class DoctorBioActivity extends EcareZoneBaseActivity {

    private static final String TAG = DoctorBioActivity.class.getSimpleName();
    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private static final int HTTP_STATUS_OK = 200;
    private Long doctorId;
    private String doctorName;
    private ProgressDialog progressDialog;
    private boolean isDocAlreadyAddded;
    private boolean fromInfo;

    @Override
    protected String getCallerName() {
        return DoctorBioActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doctor);

        Bundle bundle = getIntent().getBundleExtra(Constants.DOCTOR_BIO_DETAIL);

        onNavigationChanged(R.layout.frag_doctor_bio, bundle);
        Log.i(TAG, "bio data = " + getIntent().getBundleExtra(Constants.DOCTOR_BIO_DETAIL));
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        isDocAlreadyAddded = bundle.getBoolean(Constants.DOCTOR_ALEADY_ADDED, false);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);

            mToolBar.setNavigationIcon(R.drawable.back_);
            if (!isDocAlreadyAddded) {
                mToolBar.setOnMenuItemClickListener(
                        new Toolbar.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.action_add) {
                                    Log.i(TAG, "Menu = " + item.getTitle() + ", " + item.getItemId());
                                    if(NetworkCheck.isNetworkAvailable(getBaseContext())) {
                                        sendAddDoctorRequest();
                                    } else {
                                        Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                                    }
                                }
                                return true;
                            }
                        });
            }
        }

        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.doctor_bio));
        doctorName = ((Doctor) bundle.getParcelable(Constants.DOCTOR_DETAIL)).name;
        doctorId = ((Doctor) bundle.getParcelable(Constants.DOCTOR_DETAIL)).doctorId;
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        if(isDocAlreadyAddded){
            menu.findItem(R.id.action_add).setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_doctor_bio) {
            changeFragment(R.id.screen_container, new DoctorBioFragment(),
                    DoctorBioFragment.class.getSimpleName(), args);
        }

    }

    private void sendAddDoctorRequest() {
        Log.d(TAG, "SendAddDoctorRequest");
        progressDialog = ProgressDialogUtil.getProgressDialog(this, "Adding Doctor......");
        AddDoctorRequest request =
                new AddDoctorRequest(doctorId, doctorName, LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique);
        getSpiceManager().execute(request, new AddDoctorTaskRequestListener());
    }



    public final class AddDoctorTaskRequestListener implements RequestListener<AddDoctorResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(AddDoctorResponse addDoctorResponse) {
            progressDialog.dismiss();
            Log.d(TAG, "ResponseCode " + addDoctorResponse.status.code);

            if (addDoctorResponse.status.code == HTTP_STATUS_OK) {
                AddDoctorRequestDialog addDoctorRequestDialog = new AddDoctorRequestDialog();
                Bundle bndl = new Bundle();
                bndl.putString("doctor_name", doctorName);
                addDoctorRequestDialog.setArguments(bndl);
                FragmentManager fragmentManager = getFragmentManager();
                addDoctorRequestDialog.show(fragmentManager, "AddDoctorRequestSuccessFragment");
            } else {
                Toast.makeText(DoctorBioActivity.this, addDoctorResponse.status.message, Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false, this);
        Util.changeStatus(false, this);
    }
}
