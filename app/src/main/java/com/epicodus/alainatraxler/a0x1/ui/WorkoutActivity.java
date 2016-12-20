package com.epicodus.alainatraxler.a0x1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.FromWorkoutAdapter;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class WorkoutActivity extends BaseActivity implements DataTransferInterface, OnStartDragListener, View.OnClickListener{
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;
    @Bind(R.id.Save) Button mSave;
    @Bind(R.id.Do) Button mDo;
    @Bind(R.id.Update) Button mUpdate;
    @Bind(R.id.Delete) Button mDelete;
    @Bind(R.id.Name) EditText mName;

    private ArrayList<Workout> mWorkouts = new ArrayList<Workout>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private FromWorkoutAdapter mFromWorkoutAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ToExerciseAdapter mToAdapter;

    private String currentPushId;

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

        mToAdapter = new ToExerciseAdapter(getApplicationContext(), mExercisesTo, this, this);
        mRecyclerViewTo.setAdapter(mToAdapter);
        RecyclerView.LayoutManager ToLayoutManager =
                new LinearLayoutManager(WorkoutActivity.this);
        mRecyclerViewTo.setLayoutManager(ToLayoutManager);
        mRecyclerViewTo.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromWorkoutAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        getWorkouts();

        mSave.setOnClickListener(this);
        mDo.setOnClickListener(this);
        mUpdate.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }

    public Boolean validate(){
        if(mName.getText().toString().equals("")){
            Toast.makeText(WorkoutActivity.this, "Please name this routine", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mExercisesTo.size() == 0){
            Toast.makeText(WorkoutActivity.this, "Please select a workout", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v){
        if(v == mSave){
            if(validate()){
                Toast.makeText(WorkoutActivity.this, "New routine created", Toast.LENGTH_SHORT).show();

                Routine routine = new Routine(mName.getText().toString(), mExercisesTo);
                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                routine.setPushId(pushRef.getKey());
                pushRef.setValue(routine);
            }
        }else if(v == mDo){

        }else if(v == mDelete){
            if(currentPushId != null){
                dbCurrentUser.child(Constants.DB_NODE_WORKOUTS).child(currentPushId).removeValue();

                int catcher = mExercisesTo.size();
                mToAdapter.resetExercises();
                mExercisesTo.clear();
                mToAdapter.notifyItemRangeRemoved(0, catcher);

                mWorkouts.clear();
                getWorkouts();
            }else{
                Toast.makeText(WorkoutActivity.this, "No workout selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void setValues(Exercise exercise) {}

    @Override
    public void setRoutine(Routine routine){}

    @Override
    public void setString(String string){}

    @Override
    public void setObject(Object object){
        Workout workout = (Workout) object;
        currentPushId = workout.getPushId();

        int catcher = mExercisesTo.size();
        mToAdapter.resetExercises();
        mExercisesTo.clear();
        mToAdapter.notifyItemRangeRemoved(0, catcher);

        for(int i = 0; i < workout.getExercises().size(); i++){
            mExercisesTo.add(workout.getExercises().get(i));
        }
        mToAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

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
