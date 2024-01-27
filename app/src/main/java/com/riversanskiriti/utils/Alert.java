package com.riversanskiriti.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.riversanskiriti.prarang.R;

/**
 * Created by ARPIT on 06-01-2017.
 */

public class Alert {
    Context mContext;
    Listener alertListner;

    public Alert(Context context, Listener listener) {
        mContext = context;
        alertListner = listener;
    }

    public Alert(Context context) {
        mContext = context;
    }

    public void show(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    public void confirm(String title, String message, final int requestCode) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alertListner != null) {
                    alertListner.onAlertListener(which, requestCode);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alertListner != null) {
                    alertListner.onAlertListener(which, requestCode);
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void showList(String title, CharSequence[] list, final int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (alertListner != null) {
                    alertListner.onAlertListener(which, requestCode);
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public interface Listener {
        public void onAlertListener(int index, int requestCode);
    }
}
