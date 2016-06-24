package com.ecarezone.android.patient.fragment.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by 10603675 on 23-06-2016.
 */
public class EcareZoneAlertDialog {

    public static void showAlertDialog(Context context, String title, String message,
                                String positiveBtnText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);
        if (positiveBtnText != null) {
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.show();
    }
}
