package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.epicodus.alainatraxler.a0x1.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class YouActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.Workouts) Button mWorkouts;
    @Bind(R.id.Routines) Button mRoutines;
    @Bind(R.id.Profile) Button mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you);
        ButterKnife.bind(this);

        mWorkouts.setOnClickListener(this);
        mRoutines.setOnClickListener(this);
        mProfile.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == mWorkouts){
            Intent intent = new Intent(YouActivity.this, WorkoutActivity.class);
            startActivity(intent);
        }if(v == mRoutines){
            Intent intent = new Intent(YouActivity.this, RoutineActivity.class);
            startActivity(intent);
        }
    }
}
