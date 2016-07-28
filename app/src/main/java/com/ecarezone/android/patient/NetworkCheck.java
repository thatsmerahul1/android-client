package com.ecarezone.android.patient;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Namitha on 6/6/2016.
 */
public class NetworkCheck {
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
