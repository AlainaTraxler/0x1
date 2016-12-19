package com.epicodus.alainatraxler.a0x1.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Guest on 12/19/16.
 */
public class MyApplication extends Application {
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        if(mSharedPreferences.getString("Remember", null) == null){
            FirebaseAuth.getInstance().signOut();
        }
    }
}
