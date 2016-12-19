package com.epicodus.alainatraxler.a0x1;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

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

        }else if(v == mLogin){

        }
    }
}
