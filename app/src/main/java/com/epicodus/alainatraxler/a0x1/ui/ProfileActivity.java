package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Workout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
    }

    @Override
    public void onClick(View v){
        if(v == mLogout){
            mCurrentUser = null;
            mAuth.signOut();
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

}
