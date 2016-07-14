package com.ecarezone.android.patient;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.UpdatePasswordRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.utils.PasswordUtil;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class UpdatePasswordActivity extends EcareZoneBaseActivity {
    private static String TAG = UpdatePasswordActivity.class.getSimpleName();
    private EditText mEditTextCurrentPwd = null;
    private EditText mEditTextNewPwd = null;
    private EditText mEditTextConfirmPwd = null;
    private TextView mTextViewerror = null;
    private Toolbar mToolBar = null;
    private ProgressDialog progressDialog;
    private ActionBar mActionBar = null;
    private String newPwd;
    private String currentPwd;

    @Override
    protected String getCallerName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_update_password);
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.back_);
            mToolBar.setOnMenuItemClickListener(
                    new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            currentPwd = mEditTextCurrentPwd.getEditableText().toString();
                            newPwd = mEditTextNewPwd.getEditableText().toString();
                            String confirmPwd = mEditTextConfirmPwd.getEditableText().toString();

                            if (TextUtils.isEmpty(newPwd) || (newPwd.trim().length() < 8) ||!newPwd.equals(confirmPwd)) {
                                mTextViewerror.setVisibility(View.VISIBLE);
                                if(newPwd.length() != 0 || confirmPwd.length() != 0 || !newPwd.equals(confirmPwd) ) {
                                    if((newPwd.trim().length() < 8)) {
                                        mTextViewerror.setText(getString(R.string.error_password_less_than_registration));
                                    } else {
                                        mTextViewerror.setText(getString(R.string.password_mismatch));
                                    }
                                } else {
                                    mTextViewerror.setText(getString(R.string.error_password_less_than_registration));
                                }
                            } else {
                                doPasswordUpdate();
                            }
                            return true;
                        }
                    });
        }
        mEditTextCurrentPwd = (EditText) findViewById(R.id.edit_text_current_pwd);
        mEditTextNewPwd = (EditText) findViewById(R.id.edit_text_new_pwd);
        mEditTextConfirmPwd = (EditText) findViewById(R.id.edit_text_confirm_pwd);
        mTextViewerror = (TextView) findViewById(R.id.textview_error);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.reset_password));
        addSupportOnBackStackChangedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        MenuItem checkItem = menu.findItem(R.id.action_check);
        checkItem.setEnabled(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_check) {

        } else if (item.getItemId() == android.R.id.home) {
            Log.d("Naga", "Getting Back");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doPasswordUpdate() {
        Log.d("Naga", "Password Requesting from doPasswordUpdate");
        currentPwd = PasswordUtil.getHashedPassword(currentPwd);
        newPwd = PasswordUtil.getHashedPassword(newPwd);
        UpdatePasswordRequest request = new UpdatePasswordRequest(currentPwd, newPwd,
                LoginInfo.userName, LoginInfo.role);
        progressDialog = ProgressDialogUtil.getProgressDialog(this, getString(R.string.progress_dialog_loading));
        getSpiceManager().execute(request, new DoUpdatePasswordRequestListener());
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {

    }

    public final class DoUpdatePasswordRequestListener implements RequestListener<BaseResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(final BaseResponse baseResponse) {

            progressDialog.dismiss();
            mTextViewerror.setVisibility(View.VISIBLE);
            mTextViewerror.setText(baseResponse.status.message);
            if (baseResponse.status.code == 200) {
                LoginInfo.hashedPassword = newPwd;
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
    }
}
