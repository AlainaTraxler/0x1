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
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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
                mCurrentUser = mAuth.getCurrentUser();
                if (mCurrentUser != null && mContext instanceof LoginActivity) {
                    Log.v(TAG, "Redirecting to main");
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if(mCurrentUser == null && !(mContext instanceof LoginActivity)){
                    Log.v(TAG, "Redirecting to login");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    public Boolean validateName(String name) {
        if (name.equals("")) {
            Toast.makeText(mContext, "Please name this routine", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean validateSelected(ArrayList<Exercise> exercises){
        if(exercises.size() == 0){
            Toast.makeText(mContext, "No exercises selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean validateFields(ArrayList<Exercise> exercises){
        for(int i = 0; i < exercises.size(); i++){
            Exercise exercise = exercises.get(i);
            if(exercise.getType().equals(Constants.TYPE_WEIGHT)){
                if(exercise.getSets() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of sets for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(exercise.getReps() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of reps for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(exercise.getWeight() <= 0){
                    Toast.makeText(mContext, "Please enter a valid weight for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                String time = exercise.getTime();
                if(time.length() == 0 || !time.contains(":")){
                    return false;
                }else{
                    String minutes = time.substring(0,time.indexOf(":"));
                    String seconds = time.substring(time.indexOf(":") + 1, time.length());

                    if(minutes.length() <=0 || seconds.length() != 2 || Integer.parseInt(minutes) + Integer.parseInt(seconds) <= 0){
                        Toast.makeText(BaseActivity.this, "Please enter a valid time for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if(exercise.getDistance() <= 0){
                        Toast.makeText(mContext, "Please enter a valid distance for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }else if(exercise.getType().equals(Constants.TYPE_BODYWEIGHT)){
                if(exercise.getSets() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of sets for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(exercise.getReps() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of reps for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    public Boolean validateFieldsAllowEmpty(ArrayList<Exercise> exercises){
        for(int i = 0; i < exercises.size(); i++){
            Exercise exercise = exercises.get(i);
            if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                String time = exercise.getTime();

                if(time.length() == 0){
                    return true;
                }else if(!time.contains(":")){
                    Toast.makeText(BaseActivity.this, "Please enter a valid time for " + exercise.getName() + ", or leave blank", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    String minutes = time.substring(0,time.indexOf(":"));
                    String seconds = time.substring(time.indexOf(":") + 1, time.length());

                    if(minutes.length() <=0 || seconds.length() != 2){
                        Toast.makeText(BaseActivity.this, "Please enter a valid time for " + exercise.getName() + ", or leave blank", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
