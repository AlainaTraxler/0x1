package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public Context mContext;
    public FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser mCurrentUser;

    public String TAG;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;

    public DatabaseReference db;
    public DatabaseReference dbUsers;
    public DatabaseReference dbCurrentUser;
    public DatabaseReference dbExercises;
    public DatabaseReference dbRoutines;
    public DatabaseReference dbWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mContext = this;
        mCurrentUser = mAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference();
        dbUsers = db.child(Constants.DB_NODE_USERS);
        dbExercises = db.child(Constants.DB_NODE_EXERCISES);
        dbRoutines = dbUsers.child(Constants.DB_NODE_ROUTINES);
        dbWorkouts = dbUsers.child(Constants.DB_NODE_WORKOUTS);

        TAG = this.getClass().getSimpleName();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        if(mCurrentUser != null){
            dbCurrentUser = dbUsers.child(mAuth.getCurrentUser().getUid());
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

    public void overrideFonts(final Context context, final View v, String font) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, font);
                }
            } else if (v instanceof TextView || v instanceof Button) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + font));
            }
        } catch (Exception e) {
            }
    }
}
