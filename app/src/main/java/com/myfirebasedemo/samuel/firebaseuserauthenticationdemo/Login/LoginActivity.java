package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.CreateNewUser.SignUpActivity;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.Home.HomeActivity;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.R;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import me.drakeet.materialdialog.MaterialDialog;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private MaterialTextField emailField, passwordField;
    private Button loginBtn, createUserBtn;
    private TextView recoverPasswordBtn;
    private EditText rpEmailField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private AVLoadingIndicatorView myLoadingIndicatorView, rpIndicatorView;
    private String email, password;
    private MaterialDialog materialDialog;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Login Activity Started!!");

        /**
         * this will directly close the app on backPress if the user is in Home (logged in).
         */
        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }

        /**
         * initializing widgets
         */
        emailField = (MaterialTextField) findViewById(R.id.mtf_email_field);
        passwordField = (MaterialTextField) findViewById(R.id.mtf_password_field);
        loginBtn = (Button) findViewById(R.id.btn_sign_in);
        createUserBtn = (Button) findViewById(R.id.btn_create_account);
        recoverPasswordBtn = (TextView) findViewById(R.id.tv_recover_password);
        myLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi_loadingIndicator);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.myCoordinate_layout);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: User is logged in.");
                    myLoadingIndicatorView.hide();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Log.d(TAG, "onAuthStateChanged: going to HomeActivity.");
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: User is NOT logged in.");
                }
            }
        };


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Login button is clicked");
                signInUser();
            }
        });

        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailField.getEditText().setText("");
                passwordField.getEditText().setText("");
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        recoverPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Create user button working properly");
                materialDialog = new MaterialDialog(LoginActivity.this);
                materialDialog.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.dialog_background));
                materialDialog.setCanceledOnTouchOutside(false);
                View myView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_recover_password, null);
                materialDialog.setView(myView);
                rpEmailField = (EditText) myView.findViewById(R.id.edt_RecoverPassEmail);
                rpIndicatorView = (AVLoadingIndicatorView) myView.findViewById(R.id.avi_loadingIndicator);
                Button positiveBtn = (Button) myView.findViewById(R.id.myPositiveBtn);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeKyboardWhenFinished();
                        rpIndicatorView.smoothToShow();
                        email = rpEmailField.getText().toString().trim();
                        if (!TextUtils.isEmpty(email)){
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        rpIndicatorView.hide();
                                        materialDialog.dismiss();
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Password reset link has been sent to "+email, 5000);
                                        View sbView = snackbar.getView();
                                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                        textView.setTextColor(Color.GREEN);
                                        snackbar.show();
                                    }
                                    else {
                                        rpIndicatorView.hide();
                                        materialDialog.dismiss();
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
                            rpIndicatorView.hide();
                            rpEmailField.setError("Please enter your email address");
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
                emailField.getEditText().setText("");
                passwordField.getEditText().setText("");
            }
        });

    }

    /**
     * function to signIn user
     */
    private void signInUser(){
        closeKyboardWhenFinished();
        myLoadingIndicatorView.smoothToShow();
        email = emailField.getEditText().getText().toString().trim();
        password = passwordField.getEditText().getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        myLoadingIndicatorView.hide();
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        emailField.getEditText().setText("");
                        passwordField.getEditText().setText("");
                    }
                }
            });
        }
        else {
            myLoadingIndicatorView.hide();
            if (!emailField.isExpanded() || !passwordField.isExpanded()){
                emailField.expand();
                passwordField.expand();
            }
            if (TextUtils.isEmpty(email)){
                emailField.getEditText().setError("This field cannot be empty");
            }
            if (TextUtils.isEmpty(password)){
                passwordField.getEditText().setError("This field cannot be empty");
            }
        }
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
