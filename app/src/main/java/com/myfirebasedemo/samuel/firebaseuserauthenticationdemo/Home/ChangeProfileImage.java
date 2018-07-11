package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.Home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.R;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class ChangeProfileImage extends AppCompatActivity {

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private CircleImageView myImageView;
    private Button cancelBtn, uploadBtn, selectBtn;
    private Uri mImageUri;
    private AVLoadingIndicatorView loadingIndicator;
    MaterialDialog dialog;
    public static final int PICK_IMAGE = 1;
    private int STORAGE_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_image);

        myImageView = (CircleImageView) findViewById(R.id.my_imageView);
        cancelBtn = (Button) findViewById(R.id.btn_Cancel);
        uploadBtn = (Button) findViewById(R.id.btn_uploadImage);
        selectBtn = (Button) findViewById(R.id.btn_selectImage);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.my_loadingIndicator);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImageView.setImageResource(R.color.colorPrimaryDark);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ChangeProfileImage.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(ChangeProfileImage.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } else {
                    requestStoragePermission();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null){
                Toast.makeText(ChangeProfileImage.this, "Failed!", Toast.LENGTH_LONG).show();
            }
            else {
                mImageUri = data.getData();
                Picasso.get().load(mImageUri).centerCrop().fit().into(myImageView);
            }
        }
    }

    private void uploadImage(){
        loadingIndicator.smoothToShow();
        if (mImageUri != null){
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference fileReference = mStorageReference.child("profileImages").child(firebaseUser.getUid()).child("profile_image");
            fileReference.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(task.getResult().getDownloadUrl())
                                .build();

                        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingIndicator.hide();
                                    Toast.makeText(ChangeProfileImage.this, "Profile Image Uploaded", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    loadingIndicator.hide();
                                    Toast.makeText(ChangeProfileImage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else {
                        loadingIndicator.hide();
                        Toast.makeText(ChangeProfileImage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            loadingIndicator.hide();
            Toast.makeText(ChangeProfileImage.this, "please select any image", Toast.LENGTH_LONG).show();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            dialog = new MaterialDialog(ChangeProfileImage.this);
            dialog.setTitle("Permission needed")
                    .setMessage("This permission is needed to read your storage to upload profile image.")
                    .setPositiveButton("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(ChangeProfileImage.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
