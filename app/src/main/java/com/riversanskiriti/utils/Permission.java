package com.riversanskiriti.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by ARPIT on 07-01-2017.
 */

public class Permission {
    private Context mContext;

    public Permission(Context context) {
        mContext = context;
    }

    private int requestCode = 0;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public boolean chckSelfPermission(String permissionName) {
        //Up to LOLIPOP return true
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(mContext, permissionName);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        //If permission is not granted returning false
        return false;
    }

    public boolean checkSelfPermissionMultiple(String[] permissions) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            // Permissions are not required for SDK versions below Lollipop (21 and below)
            return true;
        }

        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(mContext, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    //Requesting permission
    public void requestPermissions(String permissionName, Fragment fragment) {
        if (fragment != null) {
            if (fragment.shouldShowRequestPermissionRationale(permissionName)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
            }
            //And finally ask for the permission
            fragment.requestPermissions(new String[]{permissionName}, requestCode);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) mContext, permissionName)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((AppCompatActivity) mContext, new String[]{permissionName}, requestCode);
        }
        requestCode = 0;
    }

    //Requesting permission
    public void requestPermissions(String permissionName, Fragment fragment, int requestCode) {
        if (fragment != null) {
            if (fragment.shouldShowRequestPermissionRationale(permissionName)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
            }
            //And finally ask for the permission
            fragment.requestPermissions(new String[]{permissionName}, requestCode);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) mContext, permissionName)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((AppCompatActivity) mContext, new String[]{permissionName}, requestCode);
        }
    }

    public void requestPermissionMultiple(String[] permissions, Fragment fragment) {
        if (fragment != null) {
            if (shouldShowRequestPermissionRationale(fragment, permissions)) {
                // Explain why you need these permissions
            }
            // Finally, ask for the permissions
            fragment.requestPermissions(permissions, requestCode);
        } else {
            if (shouldShowRequestPermissionRationale((AppCompatActivity) mContext, permissions)) {
                // Explain why you need these permissions
            }
            // Finally, ask for the permissions
            ActivityCompat.requestPermissions((AppCompatActivity) mContext, permissions, requestCode);
        }
        requestCode = 0;
    }

    private boolean shouldShowRequestPermissionRationale(Fragment fragment, String[] permissions) {
        for (String permission : permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldShowRequestPermissionRationale(AppCompatActivity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

}
