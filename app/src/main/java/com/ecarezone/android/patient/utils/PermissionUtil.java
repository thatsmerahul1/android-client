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
    public static final int REQUEST_CODE_ASK_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 101;
    public static final int REQUEST_CODE_ASK_CAPTURE_PHOTO_PERMISSIONS = 102;

    private static String sinchPermissions[] = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };

    private static String mCapturePhotoFromCameraPermissions[] = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int SINCH_PERMISSIONS = 1;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSIONS = 2;
    public static final int CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS = 3;


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
            case WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                break;
            case CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS:
                permissions = mCapturePhotoFromCameraPermissions;
                break;
        }
        return permissions;
    }
}
