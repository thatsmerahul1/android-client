package com.ecarezone.android.patient.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by L&T Technology Services on 2/26/2016.
 */
public class ProgressDialogUtil {

    public static ProgressDialog getProgressDialog(Activity activity, String displayString) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(displayString);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        return progressDialog;
    }
}