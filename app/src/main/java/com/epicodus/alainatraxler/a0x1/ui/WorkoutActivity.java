package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.FromWorkoutAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.models.Workout;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.SimpleItemTouchHelperCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class WorkoutActivity extends BaseActivity implements DataTransferInterface{
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;

    private ArrayList<Workout> mWorkouts = new ArrayList<Workout>();
    private FromWorkoutAdapter mFromWorkoutAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);

        mFromWorkoutAdapter = new FromWorkoutAdapter(getApplicationContext(), mWorkouts, this);
        mRecyclerViewFrom.setAdapter(mFromWorkoutAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(WorkoutActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromWorkoutAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        getWorkouts();
    }

    @Override
    public void setValues(Exercise exercise) {}

    @Override
    public void setRoutine(Routine routine){}

    @Override
    public void setString(String string){}

    @Override
    public void setObject(Object object){}

    public void getWorkouts(){
        dbCurrentUser.child(Constants.DB_NODE_WORKOUTS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Workout workout = dataSnapshot.getValue(Workout.class);
                mWorkouts.add(workout);
                mFromWorkoutAdapter.notifyDataSetChanged();
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
