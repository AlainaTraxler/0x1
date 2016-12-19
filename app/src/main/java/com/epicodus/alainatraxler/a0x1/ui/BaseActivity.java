package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public Context mContext;
    public FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser mCurrentUser;

    public String TAG;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mContext = this;
        mCurrentUser = mAuth.getCurrentUser();

        TAG = this.getClass().getSimpleName();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        if(mCurrentUser != null){
            Log.v(TAG, mAuth.getCurrentUser().getEmail());
        }else{
            Log.v(TAG, "No user logged in");
        }

        // Migrate to Application level

        // Checks to see if user is logged in and redirects them as needed
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mCurrentUser != null && mContext instanceof LoginActivity) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                } else if(mCurrentUser == null && !(mContext instanceof LoginActivity)){
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
