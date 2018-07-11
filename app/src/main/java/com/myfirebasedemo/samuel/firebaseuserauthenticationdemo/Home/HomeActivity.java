package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.Login.LoginActivity;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.R;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.UserDatabaseModel;
import com.myfirebasedemo.samuel.firebaseuserauthenticationdemo.UserManagemant;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Toolbar myToolbar;
    private CircleImageView profileImageView;
    private TextView emailDisplay, nameDisplay, phoneDisplay, emailVerificationDisplay;
    private NavigationView mNavView;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private UserManagemant mUserManagemant;
    private AVLoadingIndicatorView userDataLoadingIndicator, userDataLoadingIndicator2;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: Home Activity Started!!");

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar_layout);
        profileImageView = (CircleImageView) findViewById(R.id.civ_profileImage_field);
        emailDisplay = (TextView) findViewById(R.id.tv_emailDisplay_field);
        nameDisplay = (TextView) findViewById(R.id.tv_nameDisplay_field);
        phoneDisplay = (TextView) findViewById(R.id.tv_phoneNoDisplay_field);
        emailVerificationDisplay = (TextView) findViewById(R.id.tv_emailVerificationDisp_field);
        userDataLoadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.userData_loadingIndicator);
        userDataLoadingIndicator2 = (AVLoadingIndicatorView) findViewById(R.id.userData_loadingIndicator2);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.myCoordinate_layout);

        /**
         * setting up toolbar
         */
        myToolbar.setTitle("Authentication & Management Demo");
        setSupportActionBar(myToolbar);
        getSupportActionBar();

        setupDrawerLayout();

        mUserManagemant = new UserManagemant(HomeActivity.this, coordinatorLayout);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: User is logged in.");
                    displayUserData(user);
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: User is NOT logged in.");
                    finish();
                }
            }
        };
    }

    /**
     * setting up drawer layout.
     */
    private void setupDrawerLayout(){
        mDrawer = (DrawerLayout) findViewById(R.id.mDrawerLayout_home);
        mToggle = new ActionBarDrawerToggle(HomeActivity.this, mDrawer, myToolbar, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavView = (NavigationView) findViewById(R.id.mNavigationView_home);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.m_changeName){
                    mDrawer.closeDrawers();
                    mUserManagemant.changeName(LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_change_name, null));
                    displayUserData(mUserManagemant.getUser());
                }
                else if (itemId == R.id.m_changeEmail){
                    mDrawer.closeDrawers();
                    mUserManagemant.changeEmail(LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_change_email, null));
                    displayUserData(mUserManagemant.getUser());
                }
                else if (itemId == R.id.m_changePassword){
                    mDrawer.closeDrawers();
                    mUserManagemant.changePassword(LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_change_password, null));
                }
                else if (itemId == R.id.m_changePhoneNo){
                    mDrawer.closeDrawers();
                    mUserManagemant.changePhoneNo(LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_change_phoneno, null));
                    displayUserData(mUserManagemant.getUser());
                }
                else if (itemId == R.id.m_verifyEmail){
                    mDrawer.closeDrawers();
                    mUserManagemant.verifyEmail();
                    displayUserData(mUserManagemant.getUser());
                }
                else if (itemId == R.id.m_changeProfilePhoto){
                    mDrawer.closeDrawers();
                    Intent intent = new Intent(HomeActivity.this, ChangeProfileImage.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.m_deleteAccount){
                    mDrawer.closeDrawers();
                    mUserManagemant.deleteAccount(LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_delete_account, null));
                }
                return false;
            }
        });
    }

    /**
     * displaying user data from database.
     */
    private void displayUserData(final FirebaseUser user){
        userDataLoadingIndicator.smoothToShow();
        userDataLoadingIndicator2.smoothToShow();
        mDatabaseReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    UserDatabaseModel userDatabaseModel = dataSnapshot.getValue(UserDatabaseModel.class);
                    userDataLoadingIndicator.hide();
                    userDataLoadingIndicator2.hide();
                    nameDisplay.setText(userDatabaseModel.getName());
                    emailDisplay.setText(userDatabaseModel.getEmail());
                    if (user.isEmailVerified()){
                        emailVerificationDisplay.setVisibility(View.INVISIBLE);
                    }
                    else {
                        emailVerificationDisplay.setText("(not verified)");
                    }
                    phoneDisplay.setText(userDatabaseModel.getPhoneNo());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userDataLoadingIndicator.hide();
                userDataLoadingIndicator2.hide();
            }
        });
        Picasso.get().load(user.getPhotoUrl()).fit().centerCrop().into(profileImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * if (mToggle.onOptionsItemSelected(item)){
         return true;
         }
         */
        int id = item.getItemId();
        if(id == R.id.men_signout){
            mDrawer.closeDrawers();
            mAuth.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
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
