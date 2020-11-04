package com.twopibd.dactarbari.ambulance.utils;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.twopibd.dactarbari.ambulance.R;

public class MyDialog {
    public interface confirmListener {
        void onDialogClicked(boolean result);
    }
    public void yesNoConfirmation(confirmListener listener, String message) {



//        final Dialog dialog = new Dialog(activity);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        dialog.setContentView(R.layout.yes_no_dialog);
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
//        TextView tv_msg = (TextView) dialog.findViewById(R.id.tv_msg);
//        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
//        tv_msg.setText(message);
//
//        tv_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onDialogClicked(true);
//
//
//                dialog.dismiss();
//            }
//        });
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                listener.onDialogClicked(false);
//
//                dialog.dismiss();
//            }
//        });


    }
}
