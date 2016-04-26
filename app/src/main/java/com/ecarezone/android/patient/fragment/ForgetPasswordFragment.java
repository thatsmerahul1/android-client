package com.ecarezone.android.patient.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextView textView_error;
    private Button buttonSend;

    public static ForgetPasswordFragment newInstance() {
        return new ForgetPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.act_forgotpassword, container, false);
        editTextForgetPwd = (EditText) view.findViewById(R.id.editText_ForgetPwd);
        buttonSend = (Button) view.findViewById(R.id.button_send);
        textView_error=(TextView)view.findViewById(R.id.textview_error);
        buttonSend.setOnClickListener(this);
        editTextForgetPwd.addTextChangedListener(mTextWatcher);
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
            String email=editTextForgetPwd.getText().toString();
            textView_error.setVisibility(View.INVISIBLE);
            if (TextUtils.isEmpty(email)
                    || (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) ) {
                textView_error.setVisibility(View.VISIBLE);
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

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0){
                buttonSend.setEnabled(true);
            }
            else{
                buttonSend.setEnabled(false);
            }
            buttonSend.getBackground().clearColorFilter();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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

            }
            textView_error.setVisibility(View.VISIBLE);
            textView_error.setText(loginResponse.status.message);
            progressDialog.dismiss();

        }
    }
}
