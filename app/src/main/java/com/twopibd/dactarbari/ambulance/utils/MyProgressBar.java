package com.twopibd.dactarbari.ambulance.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by mukul on 3/17/2019.
 */

public class MyProgressBar {
   public static String msg = "Please wait";
   public static ProgressDialog progressDialog;

    public static ProgressDialog with(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(false);
        return progressDialog;


    }

    public static void setMessage(String message) {
        progressDialog.setMessage(message);



    }
    public static void dismiss( ) {
       if (progressDialog.isShowing()){
           progressDialog.dismiss();
       }



    }
}
