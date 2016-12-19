package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.epicodus.alainatraxler.a0x1.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.System) Button mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSystem.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == mSystem){
            Intent intent = new Intent(MainActivity.this, SystemActivity.class);
            startActivity(intent);
        }
    }
}
