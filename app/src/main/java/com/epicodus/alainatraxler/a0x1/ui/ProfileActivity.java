package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Workout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.Workouts) TextView mWorkouts;
    @Bind(R.id.Routines) TextView mRoutines;
    @Bind(R.id.WeightMoved) TextView mWeightMoved;
    @Bind(R.id.DistanceMoved) TextView mDistanceMoved;
    @Bind(R.id.Reps) TextView mReps;
    @Bind(R.id.Sets) TextView mSets;
    @Bind((R.id.Logout)) Button mLogout;

    long mTotalWeightMoved = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setStats();

        mLogout.setOnClickListener(this);
        mDistanceMoved.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == mLogout){
            mCurrentUser = null;
            mAuth.signOut();
        }else if(v == mDistanceMoved){
            if(mCurrentUser.getEmail().equals("eloavox@gmail.com")){
                seedExercisesFromTextFile();
            }
        }
    }

    public void setStats(){
        dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int workouts = 0;
                int routines = 0;
                Double weightMoved = 0.0;
                Double distanceMoved = 0.0;
                int reps = 0;
                int sets = 0;


                for(DataSnapshot snapshot:dataSnapshot.child(Constants.DB_NODE_WORKOUTS).getChildren()){
                    Workout workout = snapshot.getValue(Workout.class);
                    workouts++;
                    for(Exercise exercise:workout.getExercises()){
                        if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                            distanceMoved += exercise.getDistance();
                        }else if(exercise.getType().equals(Constants.TYPE_BODYWEIGHT)){
                            reps += exercise.getReps();
                            sets += exercise.getSets();
                        }else if(exercise.getType().equals(Constants.TYPE_WEIGHT)){
                            reps += exercise.getReps();
                            sets += exercise.getSets();
                            weightMoved += exercise.getWeight();
                        }
                    }
                }

                for(DataSnapshot snapshot:dataSnapshot.child(Constants.DB_NODE_ROUTINES).getChildren()){
                    routines++;
                }

                mWorkouts.setText(String.valueOf(workouts));
                mRoutines.setText(String.valueOf(routines));
                mWeightMoved.setText(String.valueOf(weightMoved));
                mDistanceMoved.setText(String.valueOf(distanceMoved));
                mReps.setText(String.valueOf(reps));
                mSets.setText(String.valueOf(sets));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void seedExercisesFromTextFile(){
        Toast.makeText(ProfileActivity.this, "Seed active", Toast.LENGTH_SHORT).show();

        dbExercises.removeValue();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("seedlists/exercises.txt")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                Log.v(TAG, mLine);
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
