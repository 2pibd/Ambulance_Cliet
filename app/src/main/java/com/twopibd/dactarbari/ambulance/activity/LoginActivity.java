package com.twopibd.dactarbari.ambulance.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.utils.MyProgressBar;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tv_wait)
    TextView tv_wait;
    @BindView(R.id.ed_number)
    EditText ed_number;
    @BindView(R.id.ed_code_number)
    EditText ed_code_number;
    @BindView(R.id.relativeEnterNumberVerification)
    RelativeLayout relativeEnterNumberVerification;
    @BindView(R.id.relativeGetStarted)
    RelativeLayout relativeGetStarted;
    @BindView(R.id.backIcon_enter_phone)
    ImageView backIcon_enter_phone;
    @BindView(R.id.relativeotpVerification)
    RelativeLayout relativeotpVerification;
    @BindView(R.id.backIcon_enter_otp)
    ImageView backIcon_enter_otp;
    @BindView(R.id.cardVerifyOTP)
    CardView cardVerifyOTP;
    @BindView(R.id.cardVerifyNumber)
    CardView cardVerifyNumber;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth firebaseAuth;
    String mVerificationId;
    String VERIFICATION_PHONE_NUMBER;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if ( FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getBaseContext(), LiteHomeActivity.class));
            finish();
        } else {

            relativeotpVerification.animate().translationX(relativeotpVerification.getWidth());
            relativeEnterNumberVerification.animate().translationX(relativeotpVerification.getWidth());
            firebaseAuth = FirebaseAuth.getInstance();
        }

    }

    public void compare_pin(View view) {

        if (ed_code_number.getText().toString().trim().length() > 3) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, ed_code_number.getText().toString().trim());
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void request_sms_varification(String phoneNumber) {
        //   progressDial.show();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(LoginActivity.this, "Invalid request", Toast.LENGTH_LONG).show();
                    MyProgressBar.dismiss();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    MyProgressBar.dismiss();
                    Toast.makeText(LoginActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_LONG).show();

                }else {
                    MyProgressBar.dismiss();
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                tv_wait.setText("A pin is sending to " + phoneNumber + " Enter The Varification Code You Have Received Through SMS");

                VERIFICATION_PHONE_NUMBER = phoneNumber;
                MyProgressBar.dismiss();
                //new code
                relativeotpVerification.animate().translationX(0);
                relativeEnterNumberVerification.animate().translationX(-relativeEnterNumberVerification.getWidth());


            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(


                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        final FirebaseUser user = task.getResult().getUser();

                        // presentActivity();

                        //all done
                        // Intent i = new Intent(PhonVarificationActivity.this, SignUpActivity.class);
                        //   i.putExtra("code", credential.getSmsCode());
                        //  i.putExtra("number", ed_number.getText().toString().trim());
                        // i.putExtra("fb_id", user.getUid());
                        // CachedData.CODE= credential.getSmsCode();
                        //  CachedData.NUMBER=ed_number.getText().toString().trim().replace("+","");
                        //  CachedData.fb_id=user.getUid();


                        // startActivity(i);
                        // finish();

                        Log.i("mkl", user.toString());
                        Log.i("mkl", user.getUid());

                        Toast.makeText(this, "Successfully verified", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getBaseContext(), LiteHomeActivity.class));

                    } else {
                        //  Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            //  MyUtils.getInstance().buildSnackMessage(snackView,"Invalid code !!!",LENGTH_LONG).show();
                            // Toasty.error(Phone_Varification_Activity.this,)
                            //  Toasty.error(PhonVarificationActivity.this,"Invalid code !!!",Toast.LENGTH_LONG,true).show();
                        }
                        // pDialog.hide();
                    }
                });
    }

    @OnClick(R.id.cardGetStarted)
    public void openPhoneNumberPage() {
        //works fine
        relativeGetStarted.animate().translationX(-relativeGetStarted.getWidth());
        relativeotpVerification.animate().translationX(0);
    }

    @OnClick(R.id.backIcon_enter_phone)
    public void openGetStarted() {
        //enter phone number back icon
        //works fine
        relativeGetStarted.animate().translationX(0);
        relativeotpVerification.animate().translationX(relativeotpVerification.getWidth());
    }

    @OnClick(R.id.backIcon_enter_otp)
    public void openEnterNumber() {
        //enter phone number back icon
        // relativeotpVerification.animate().translationX(relativeotpVerification.getWidth());
        relativeEnterNumberVerification.animate().translationX(0);
    }

    @OnClick(R.id.cardVerifyNumber)
    public void openOtpVerify() {
        //enter phone number back icon
        //works fine
        String number=ed_number.getText().toString().trim();
        if (number.length()>0) {
            MyProgressBar.with(context);
            request_sms_varification("+" + number);
        }
        //   relativeotpVerification.animate().translationX(0);
        //  relativeEnterNumberVerification.animate().translationX(-relativeEnterNumberVerification.getWidth());
    }
}
