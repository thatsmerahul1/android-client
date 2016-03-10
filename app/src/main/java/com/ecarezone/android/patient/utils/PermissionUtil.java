package com.ecarezone.android.patient.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by L&T Technology Services on 3/2/2016.
 */
public class PermissionUtil {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100;
    private static String sinchPermissions[] = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
            , Manifest.permission.READ_PHONE_STATE};

    public static final int SINCH_PERMISSIONS = 1;

    public static String[] getAllpermissionRequired(Activity activity, int permissionType) {
        List<String> permissionList = new ArrayList<>(Arrays.asList(getRequiredTypePermission(permissionType)));
        Iterator iter = permissionList.iterator();
        while (iter.hasNext()) {
            String permission = (String) iter.next();
            if (ContextCompat.checkSelfPermission(activity,
                    permission)
                    == PackageManager.PERMISSION_GRANTED) {
                iter.remove();
            }
        }
        String list[] = new String[permissionList.size()];
        return permissionList.toArray(list);
    }

    public static boolean isPermissionRequired() {
        boolean isPermissionRequired = false;
        if (Build.VERSION.SDK_INT >= 23) {
            isPermissionRequired = true;
        }
        return isPermissionRequired;
    }

    public static void setAllPermission(Activity activity, int requestCode, int permissionType) {
        ActivityCompat.requestPermissions(activity, getAllpermissionRequired(activity, permissionType), requestCode);
    }

    private static String[] getRequiredTypePermission(int type) {
        String permissions[] = null;
        switch (type) {
            case SINCH_PERMISSIONS:
                permissions = sinchPermissions;
                break;
        }

        return permissions;
    }

}
