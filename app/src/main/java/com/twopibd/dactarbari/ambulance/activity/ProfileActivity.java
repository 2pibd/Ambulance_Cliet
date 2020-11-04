package com.twopibd.dactarbari.ambulance.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.listeners.PicUploadListener;
import com.twopibd.dactarbari.ambulance.myfunctions.myfunctions;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_img)
    CircleImageView profile_img;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.ed_name)
    EditText ed_name;
    Context context = this;
    String userID;
    String userPhone;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userInfoDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        profile_img.setOnClickListener((View v) -> openImagePicker());
        //user name and photo and phone retrive from database and show  here

        userInfoDatabaseReference = firebaseDatabase.getReference("users").child(userID);

        userInfoDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("photo").exists()) {
                        String user_img = dataSnapshot.child("photo").getValue().toString();
                        Glide.with(context).load(user_img).into(profile_img);

                    }
                    if (dataSnapshot.child("name").exists()) {
                        ed_name.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.child("phone").exists()) {
                        tv_phone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                } else {
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ed_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userInfoDatabaseReference.child("name").setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void openImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(ProfileActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(context).load(resultUri).into(profile_img);
                myfunctions.uploadPhoto(resultUri, context, new PicUploadListener.myPicUploadListener() {
                    @Override
                    public String onPicUploadSucced(String link) {
                        firebaseDatabase.getReference("users").child(userID).child("photo").setValue(link);
                        firebaseDatabase.getReference("users").child(userID).child("phone").setValue(userPhone);
                        return null;
                    }

                    @Override
                    public String onPicUploadFailed(String errorMessage) {
                        return null;
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void back(View view) {
        onBackPressed();
    }
}
