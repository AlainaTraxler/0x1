package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromStartAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.SimpleItemTouchHelperCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class StartActivity extends BaseActivity implements View.OnClickListener, DataTransferInterface{
    @Bind(R.id.Source) Switch mSource;
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;

    private ArrayList<Routine> mRoutines = new ArrayList<Routine>();
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private ArrayList<String> mExerciseNames = new ArrayList<String>();
    private ArrayList<String> mRoutineNames = new ArrayList<String>();
    private FromStartAdapter mFromStartAdapter;
    private ItemTouchHelper mItemTouchHelper;

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
                    mFromStartAdapter.toggleDataset();
                }else{
                    mSource.setText("Routines");
                    mFromStartAdapter.toggleDataset();
                }
            }
        });

        mFromStartAdapter = new FromStartAdapter(getApplicationContext(), mExercises, selfCatch, mRoutines, mRoutineNames, mExerciseNames);
        mRecyclerViewFrom.setAdapter((RecyclerView.Adapter) mFromStartAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(StartActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromStartAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

//        ItemTouchHelper.Callback callbackFromExercise = new SimpleItemTouchHelperCallback(mFromExerciseAdapter);
//        mItemTouchHelper = new ItemTouchHelper(callbackFromRoutine);
//        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

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

    @Override
    public void setString(String string){}

    public void getRoutines(){
        dbCurrentUser.child(Constants.DB_NODE_ROUTINES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Routine routine = dataSnapshot.getValue(Routine.class);
                mRoutines.add(routine);
                mRoutineNames.add(routine.getName());
                mFromStartAdapter.notifyDataSetChanged();
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
                Exercise exercise = dataSnapshot.getValue(Exercise.class);
                mExercises.add(exercise);
                mExerciseNames.add(exercise.getName());
                mFromStartAdapter.notifyDataSetChanged();
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
