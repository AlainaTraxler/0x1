package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SystemActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.Seed) Button mSeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        ButterKnife.bind(this);

        mSeed.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == mSeed){
            seedExercisesFromTextFile();
        }
    }

    public void seedExercisesFromTextFile(){
        Toast.makeText(SystemActivity.this, "Seed active", Toast.LENGTH_SHORT).show();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("seedlists/exercises.txt")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                Log.v(TAG, mLine.substring(0,mLine.indexOf("[")));
                Log.v(TAG, mLine.substring(mLine.indexOf("[") + 1,mLine.indexOf("]")));
                if(mLine.contains("<")){
                    Log.v(TAG, mLine.substring(mLine.indexOf("<") + 1,mLine.indexOf(">")));
                }

            }

        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}
