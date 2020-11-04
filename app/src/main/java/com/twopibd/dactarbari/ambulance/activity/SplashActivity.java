package com.twopibd.dactarbari.ambulance.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.utils.SessionManager;

public class SplashActivity extends Activity {
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
