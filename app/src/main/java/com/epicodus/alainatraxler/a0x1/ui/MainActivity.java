package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.System) Button mSystem;
    @Bind(R.id.Build) Button mBuild;
    @Bind(R.id.You) Button mYou;
    @Bind(R.id.Start) Button mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSystem.setOnClickListener(this);
        mBuild.setOnClickListener(this);
        mYou.setOnClickListener(this);
        mStart.setOnClickListener(this);

//        overrideFonts(mContext, findViewById(android.R.id.content), Constants.FONT_MAIN);
    }

    public void onClick(View v){
        if(v == mSystem){
            Intent intent = new Intent(MainActivity.this, SystemActivity.class);
            startActivity(intent);
        }else if(v == mBuild){
            Intent intent = new Intent(MainActivity.this, BuildActivity.class);
            startActivity(intent);
        }else if(v == mYou){
            Intent intent = new Intent(MainActivity.this, YouActivity.class);
            startActivity(intent);
        }else if(v == mStart){
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
        }
    }
}
