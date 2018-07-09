package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class UserManagemant {

    private MaterialDialog materialDialog;
    private Context context;
    private View myView;
    private EditText newNameField, newEmailField, oldPasswordField, newPasswordField, phoneNoField;
    private AVLoadingIndicatorView indicatorView;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseUser user;
    private String oldPassword, newPassword, newEmail, phoneNo;
    private CoordinatorLayout coordinatorLayout;

    private void deleteUserPermanently(){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    indicatorView.hide();
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public UserManagemant(Context context, CoordinatorLayout coordinatorLayout){
        this.context = context;
        materialDialog = new MaterialDialog(context);
        materialDialog.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background));
        materialDialog.setCanceledOnTouchOutside(false);
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.mStorageReference = FirebaseStorage.getInstance().getReference();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.coordinatorLayout = coordinatorLayout;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void changeName(View view){
        myView = view;
        materialDialog.setView(myView);

        this.newNameField = (EditText) myView.findViewById(R.id.edt_changeName);
        this.indicatorView = (AVLoadingIndicatorView) myView.findViewById(R.id.avi_loadingIndicator);

        Button positiveBtn = (Button) myView.findViewById(R.id.myPositiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicatorView.smoothToShow();
                String newName = newNameField.getText().toString().trim();
                if (!TextUtils.isEmpty(newName)){
                    mDatabaseReference.child("users").child(user.getUid()).child("name").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                indicatorView.hide();
                                materialDialog.dismiss();
                            }
                            else {
                                indicatorView.hide();
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    indicatorView.hide();
                    newNameField.setError("This field cannot be empty");
                }
            }
        });
        Button negativeBtn = (Button) myView.findViewById(R.id.myNegativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    public void changeEmail(View view){
        myView = view;
        materialDialog.setView(myView);
        this.newEmailField = (EditText) myView.findViewById(R.id.edt_changeEmail);
        this.indicatorView = (AVLoadingIndicatorView) myView.findViewById(R.id.avi_loadingIndicator);
        Button positiveBtn = (Button) myView.findViewById(R.id.myPositiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicatorView.smoothToShow();
                newEmail = newEmailField.getText().toString().trim();
                if (!TextUtils.isEmpty(newEmail)){
                    mDatabaseReference.child("users").child(user.getUid()).child("email").setValue(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            indicatorView.hide();
                                            materialDialog.dismiss();
                                        }
                                        else {
                                            indicatorView.hide();
                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                indicatorView.hide();
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    indicatorView.hide();
                    newEmailField.setError("This field cannot be empty");
                }
            }
        });
        Button negativeBtn = (Button) myView.findViewById(R.id.myNegativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    public void changePassword(View view){
        myView = view;
        materialDialog.setView(myView);
        this.oldPasswordField = (EditText) myView.findViewById(R.id.edt_oldPassword_field);
        this.newPasswordField = (EditText) myView.findViewById(R.id.edt_newPassword_field);
        this.indicatorView = (AVLoadingIndicatorView) myView.findViewById(R.id.avi_loadingIndicator);
        Button positiveBtn = (Button) myView.findViewById(R.id.myPositiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicatorView.smoothToShow();
                oldPassword = oldPasswordField.getText().toString().trim();
                newPassword = newPasswordField.getText().toString().trim();
                if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)){
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            indicatorView.hide();
                                            materialDialog.dismiss();
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                        else {
                                            indicatorView.hide();
                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                indicatorView.hide();
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    indicatorView.hide();
                    if (TextUtils.isEmpty(newPassword)){
                        newPasswordField.setError("This field cannot be empty");
                    }
                    if (TextUtils.isEmpty(oldPassword)){
                        oldPasswordField.setError("This field cannot be empty");
                    }
                }
            }
        });
        Button negativeBtn = (Button) myView.findViewById(R.id.myNegativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    public void verifyEmail(){
        if (!user.isEmailVerified()){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Verification link has been sent to "+user.getEmail(), 5000);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.GREEN);
                        snackbar.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                            }
                        }, 5000);
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout,task.getException().getMessage(), 5000);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        snackbar.show();
                    }
                }
            });
        }
        else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout,"Your Email is already verified", 5000);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.GREEN);
            snackbar.show();
        }
    }

    public void changePhoneNo(View view){
        myView = view;
        materialDialog.setView(myView);
        this.indicatorView = (AVLoadingIndicatorView) myView.findViewById(R.id.avi_loadingIndicator);
        this.phoneNoField = (EditText) myView.findViewById(R.id.edt_changePhoneNo_Field);
        Button positiveBtn = (Button) myView.findViewById(R.id.myPositiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicatorView.smoothToShow();
                phoneNo = phoneNoField.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNo)){
                    mDatabaseReference.child("users").child(user.getUid()).child("phoneNo").setValue(phoneNo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                indicatorView.hide();
                                materialDialog.dismiss();
                            }
                            else {
                                indicatorView.hide();
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    indicatorView.hide();
                    phoneNoField.setError("This field cannot be empty");
                }
            }
        });
        Button negativeBtn = (Button) myView.findViewById(R.id.myNegativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }


    public void deleteAccount(View view){
        myView = view;
        materialDialog.setView(myView);
        this.indicatorView = (AVLoadingIndicatorView) myView.findViewById(R.id.avi_loadingIndicator);
        Button positiveBtn = (Button) myView.findViewById(R.id.myPositiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicatorView.smoothToShow();
                mDatabaseReference.child("users").child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mStorageReference.child("profileImages").child(user.getUid()).child("profile_image").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        mStorageReference.child("profileImages").child(user.getUid()).child("profile_image").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    deleteUserPermanently();
                                                }
                                                else {
                                                    indicatorView.hide();
                                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        deleteUserPermanently();
                                    }
                                }
                            });
                        }
                        else {
                            indicatorView.hide();
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Button negativeBtn = (Button) myView.findViewById(R.id.myNegativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }
}
