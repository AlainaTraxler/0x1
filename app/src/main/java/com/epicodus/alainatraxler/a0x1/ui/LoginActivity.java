package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.splash) ImageView mSplash;
    @Bind(R.id.email) EditText mEmail;
    @Bind(R.id.password) EditText mPassword;
    @Bind(R.id.rememberMe) CheckBox mRememberMe;
    @Bind(R.id.signup) Button mSignUp;
    @Bind(R.id.login) Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mSignUp.setOnClickListener(this);
        mLogin.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == mSignUp){
            createAccount(mEmail.getText().toString(), mPassword.getText().toString());
        }else if(v == mLogin){
            signIn(mEmail.getText().toString(), mPassword.getText().toString());
        }
    }

    private Boolean validateForm(String email, String password){
        if(email.equals("")){
            Toast.makeText(LoginActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length() < 6){
            Toast.makeText(LoginActivity.this, "Please enter a password 6 characters or longer", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm(email, password)) {
            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Account creation successful", Toast.LENGTH_SHORT).show();
                            if(mRememberMe.isChecked()){
                                mEditor.putString("Remember", "true").apply();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                            }
                        }


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Account creation failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm(email, password)) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            if(mRememberMe.isChecked()){
                                mEditor.putString("Remember", "true").apply();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                            }
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {

                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
    }
}
