package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromStartAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.ToExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.models.Workout;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;
import com.epicodus.alainatraxler.a0x1.util.SimpleItemTouchHelperCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class StartActivity extends BaseActivity implements View.OnClickListener, DataTransferInterface, OnStartDragListener{
    @Bind(R.id.Source) Switch mSource;
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;
    @Bind(R.id.Done) Button mDone;

    private ArrayList<Routine> mRoutines = new ArrayList<Routine>();
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private ArrayList<String> mExerciseNames = new ArrayList<String>();
    private ArrayList<String> mRoutineNames = new ArrayList<String>();
    private FromStartAdapter mFromStartAdapter;
    private ToExerciseAdapter mToAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        if(Parcels.unwrap(getIntent().getParcelableExtra("exercises")) != null){
            mExercisesTo = Parcels.unwrap(getIntent().getParcelableExtra("exercises"));
        }

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

        mFromStartAdapter = new FromStartAdapter(getApplicationContext(), mExercises, this, mRoutines, mRoutineNames, mExerciseNames);
        mRecyclerViewFrom.setAdapter((RecyclerView.Adapter) mFromStartAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(StartActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        mToAdapter = new ToExerciseAdapter(getApplicationContext(), mExercisesTo, this, this);
        mRecyclerViewTo.setAdapter(mToAdapter);
        RecyclerView.LayoutManager ToLayoutManager =
                new LinearLayoutManager(StartActivity.this);
        mRecyclerViewTo.setLayoutManager(ToLayoutManager);
        mRecyclerViewTo.setHasFixedSize(true);

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromStartAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        ItemTouchHelper.Callback callbackTo = new SimpleItemTouchHelperCallback(mToAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackTo);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewTo);

        getRoutines();
        getExercises();

        mDone.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == mDone){
            if(validateSelected() && validateFields()){
                Toast.makeText(StartActivity.this, "Worout completed!", Toast.LENGTH_SHORT).show();

                Workout workout = new Workout(mExercisesTo);
                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_WORKOUTS).push();
                workout.setPushId(pushRef.getKey());
                pushRef.setValue(workout);

                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setValues(Exercise exercise){
        mExercisesTo.add(exercise);
        mToAdapter.notifyDataSetChanged();
    }

    @Override
    public void setRoutine(Routine routine){

    }

    @Override
    public void setString(String string){}

    @Override
    public void setObject(Object object){}

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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

//    public Boolean validateName() {
//        if (mName.getText().toString().equals("")) {
//            Toast.makeText(StartActivity.this, "Please name this routine", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }

    public Boolean validateSelected(){
        if(mExercisesTo.size() == 0){
            Toast.makeText(StartActivity.this, "Please select a workout", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean validateFields(){
        for(int i = 0; i < mExercisesTo.size(); i++){
            Exercise exercise = mExercisesTo.get(i);
            if(exercise.getType().equals(Constants.TYPE_WEIGHT)){
                if(exercise.getSets() <= 0 || exercise.getReps() <= 0 || exercise.getWeight() <= 0){
                    Toast.makeText(StartActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                if(exercise.getTime() <= 0 || exercise.getDistance() <= 0){
                    Toast.makeText(StartActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_BODYWEIGHT)){
                if(exercise.getSets() <= 0 || exercise.getReps() <= 0){
                    Toast.makeText(StartActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }
}
