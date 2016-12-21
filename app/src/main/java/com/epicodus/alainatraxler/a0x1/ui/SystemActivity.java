package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.google.firebase.database.DatabaseReference;

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

        dbExercises.removeValue();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("seedlists/exercises.txt")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                String name = mLine.substring(0,mLine.indexOf("["));
                String type = mLine.substring(mLine.indexOf("[") + 1,mLine.indexOf("]"));

                Log.v(TAG, name);
                Log.v(TAG, type);

                Exercise exercise = new Exercise(name, type);

                if(mLine.contains("<")){
                    String altNames = mLine.substring(mLine.indexOf("<") + 1,mLine.indexOf(">"));
                    exercise.addAltName(altNames);
                }

                DatabaseReference pushRef = dbExercises.push();
                exercise.setPushId(pushRef.getKey());
                pushRef.setValue(exercise);

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
