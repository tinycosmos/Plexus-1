package com.kinshuu.plexus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import android.content.Intent;
import android.os.Bundle;


import io.chirp.chirpsdk.ChirpSDK;

import static com.kinshuu.plexus.Emergency.CHIRP_APP_KEY;
import static com.kinshuu.plexus.Emergency.CHIRP_APP_SECRET;
import static com.kinshuu.plexus.Emergency.chirp;

public class MainActivity extends AppCompatActivity {
    //Constants
    String TAG="MyLOGS";
    private static final int RC_SIGN_IN = 1;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static String mUsername="0",mUserEmail;

    //MainActivity UI
    Button BTNsignout, BTNemergency,BTNemergency2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Working");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_navbar);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0));
        BTNsignout=findViewById(R.id.BTNsignout);
        BTNemergency=findViewById(R.id.BTNemergency);
        BTNemergency2=findViewById(R.id.BTNemergency2);

        chirp = new ChirpSDK(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        startService(new Intent(getApplicationContext(), Listener.class));

        BTNemergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,com.kinshuu.plexus.Emergency.class);
                startActivity(intent);
            }
        });

        BTNemergency2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,com.kinshuu.plexus.PublicEmergency.class);
                startActivity(intent);
            }
        });


        BTNsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getApplicationContext());
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user!=null){
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: Signed In");
                    Toast.makeText(MainActivity.this, "Welcome "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    mUsername=user.getDisplayName();
                    mUserEmail = user.getEmail();
                }
                else{
                    // user is signed out
                    Toast.makeText(MainActivity.this, "Please signin", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged: User is signed out.");
                    //OnSignedOutInitialise();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.drawable.ic_logoname)
                                    .setTheme(R.style.AppThemeFirebaseAuth)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    
}
