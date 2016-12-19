package com.epicodus.alainatraxler.a0x1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mContext = this;

        checkForRedirect();
    }

    // Checks to see if user is logged in, and if not, redirects them to Login
    public void checkForRedirect(){
        if(mAuth.getCurrentUser() == null && !this.getClass().getSimpleName().equals("LoginActivity")){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }
}
