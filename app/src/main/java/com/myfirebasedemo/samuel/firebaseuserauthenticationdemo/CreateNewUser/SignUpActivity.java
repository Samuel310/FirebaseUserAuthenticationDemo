package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.CreateNewUser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.Home.HomeActivity;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.R;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.UserDatabaseModel;
import com.wang.avi.AVLoadingIndicatorView;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private MaterialTextField nameField, emailField, passwordField, confPasswordField, phoneNoField;
    private String name, email, password, confPassword, phoneNo;
    private Button btnCreateUser;
    private AVLoadingIndicatorView myLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /**
         * Initializing widgets
         */
        nameField = (MaterialTextField) findViewById(R.id.mtf_name_field);
        emailField = (MaterialTextField) findViewById(R.id.mtf_email_field);
        passwordField = (MaterialTextField) findViewById(R.id.mtf_password_field);
        confPasswordField = (MaterialTextField) findViewById(R.id.mtf_passwordConf_field);
        phoneNoField = (MaterialTextField) findViewById(R.id.mtf_phoneNo_field);
        btnCreateUser = (Button) findViewById(R.id.btn_signUp_user);
        myLoadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.myLoadingIndicator2);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: User is logged in.");
                    createUserDatabase(user);
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: User is NOT logged in.");
                }
            }
        };

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });

    }

    /**
     * Creating new user
     */
    private void createNewUser(){
        closeKyboardWhenFinished();
        myLoadingIndicator.smoothToShow();
        name = nameField.getEditText().getText().toString().trim();
        email = emailField.getEditText().getText().toString().trim();
        password = passwordField.getEditText().getText().toString().trim();
        confPassword = confPasswordField.getEditText().getText().toString().trim();
        phoneNo = phoneNoField.getEditText().getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confPassword)){
            if (password.equals(confPassword)){
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            myLoadingIndicator.hide();
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                myLoadingIndicator.hide();
                confPasswordField.getEditText().setError("Password is not matching.");
            }
        }
        else {
            myLoadingIndicator.hide();
            if(!nameField.isExpanded() || !emailField.isExpanded() || !passwordField.isExpanded() || !confPasswordField.isExpanded() || !phoneNoField.isExpanded()){
                nameField.expand();
                emailField.expand();
                passwordField.expand();
                confPasswordField.expand();
                phoneNoField.expand();
            }
            if (TextUtils.isEmpty(name)){
                nameField.getEditText().setError("This field cannot be empty.");
            }
            if (TextUtils.isEmpty(email)){
                emailField.getEditText().setError("This field cannot be empty.");
            }
            if (TextUtils.isEmpty(password)){
                passwordField.getEditText().setError("This field cannot be empty.");
            }
            if (TextUtils.isEmpty(confPassword)){
                confPasswordField.getEditText().setError("This field cannot be empty.");
            }
        }
    }

    /**
     * Creating database and storing info of user.
     */
    private void createUserDatabase(final FirebaseUser firebaseUser){
        if (TextUtils.isEmpty(phoneNo)){
            phoneNo = getString(R.string.phoneNoMessage);
        }
        Log.d(TAG, "createUserDatabase: Phone No.: "+phoneNo);
        Log.d(TAG, "createUserDatabase: Name: "+name);
        Log.d(TAG, "createUserDatabase: Email: "+email);

        UserDatabaseModel userData = new UserDatabaseModel(name, email, phoneNo);
        mDatabaseReference.child("users").child(firebaseUser.getUid()).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mStorageReference.child("defaultProfileImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        myLoadingIndicator.hide();
                                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        myLoadingIndicator.hide();
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    myLoadingIndicator.hide();
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closeKyboardWhenFinished(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

}
