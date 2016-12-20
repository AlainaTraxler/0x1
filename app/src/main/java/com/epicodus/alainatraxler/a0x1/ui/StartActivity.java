package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.FromRoutineAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StartActivity extends BaseActivity implements View.OnClickListener, DataTransferInterface{
    @Bind(R.id.Source) Switch mSource;
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;

    private ArrayList<Routine> mRoutines = new ArrayList<Routine>();
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private FromRoutineAdapter mFromRoutineAdapter;
    private FromExerciseAdapter mFromExerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        final DataTransferInterface selfCatch = this;

        mSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mSource.isChecked()){
                    mSource.setText("Exercises");
                    mRecyclerViewFrom.setAdapter(mFromExerciseAdapter);
                    RecyclerView.LayoutManager FromLayoutManager =
                            new LinearLayoutManager(StartActivity.this);
                    mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
                    mRecyclerViewFrom.setHasFixedSize(true);
                }else{
                    mSource.setText("Routines");
                    mRecyclerViewFrom.setAdapter(mFromRoutineAdapter);
                    RecyclerView.LayoutManager FromLayoutManager =
                            new LinearLayoutManager(StartActivity.this);
                    mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
                    mRecyclerViewFrom.setHasFixedSize(true);
                }
            }
        });

        mFromRoutineAdapter = new FromRoutineAdapter(getApplicationContext(), mRoutines, selfCatch);
        mRecyclerViewFrom.setAdapter(mFromRoutineAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(StartActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mFromExerciseAdapter = new FromExerciseAdapter(getApplicationContext(), mExercises, selfCatch);

        getRoutines();
        getExercises();
    }

    public void onClick(View v){
    }

    @Override
    public void setValues(Exercise exercise){

    }

    @Override
    public void setRoutine(Routine routine){

    }

    public void getRoutines(){
        dbCurrentUser.child(Constants.DB_NODE_ROUTINES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRoutines.add(dataSnapshot.getValue(Routine.class));
                mFromRoutineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getExercises(){
        dbExercises.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mExercises.add(dataSnapshot.getValue(Exercise.class));
                mFromExerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
