package com.ecarezone.android.patient.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ecarezone.android.patient.PatientApplication;
import com.ecarezone.android.patient.config.Constants;

/**
 * Created by 10603675 on 04-06-2016.
 */
public class Util {

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void changeStatus(boolean status, Activity activity){
        if(status) {
            PatientApplication.nameValuePair.put(Constants.STATUS_CHANGE, true);
        } else {
            PatientApplication.nameValuePair.put(Constants.STATUS_CHANGE, false);
        }
    }
}
