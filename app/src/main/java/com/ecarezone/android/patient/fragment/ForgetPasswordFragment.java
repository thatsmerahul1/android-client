package com.ecarezone.android.patient.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.model.rest.ForgetPassRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by L&T Technology Services  on 2/19/2016.
 */
public class ForgetPasswordFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    private EditText editTextForgetPwd;
    private ProgressDialog progressDialog;

    public static ForgetPasswordFragment newInstance() {
        return new ForgetPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.act_forgotpassword, container, false);
        editTextForgetPwd = (EditText) view.findViewById(R.id.editText_ForgetPwd);
        Button buttonSend = (Button) view.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(this);
        return view;
    }

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_send) {

            if (editTextForgetPwd.getText().length() == 0) {
                editTextForgetPwd.setError("");
                return;
            }
            //Foret Password request
            progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                    getText(R.string.progress_dialog_requesting).toString());
            ForgetPassRequest request =
                    new ForgetPassRequest(editTextForgetPwd.getText().toString(), 1);
            getSpiceManager().execute(request, new PasswordRequestListener());

        }

    }

    //Forget password response
    public final class PasswordRequestListener implements RequestListener<LoginResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(LoginResponse loginResponse) {
            /*If status is success then
              password sent to email
             */
            if (loginResponse.status.code == 200) {
                Toast.makeText(getApplicationContext(), loginResponse.status.message, Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();

        }
    }
}
