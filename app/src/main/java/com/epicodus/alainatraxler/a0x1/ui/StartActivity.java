package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class StartActivity extends BaseActivity implements View.OnClickListener, DataTransferInterface, OnStartDragListener{
    @Bind(R.id.Source) Switch mSource;
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;
    @Bind(R.id.Done) Button mDone;
    @Bind(R.id.Save) Button mSave;
    @Bind(R.id.Name) TextView mName;
    @Bind(R.id.Search) SearchView mSearch;

    private ArrayList<Routine> mRoutines = new ArrayList<Routine>();
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private ArrayList<String> mExerciseNames = new ArrayList<String>();
    private ArrayList<String> mRoutineNames = new ArrayList<String>();
    private ArrayList<Routine> mSearchArrayRoutines = new ArrayList<Routine>();
    private ArrayList<Exercise> mSearchArrayExercises = new ArrayList<Exercise>();
    private ArrayList<String> mSearchArrayRoutineNames = new ArrayList<String>();
    private ArrayList<String> mSearchArrayExcerciseNames = new ArrayList<String>();
    private FromStartAdapter mFromStartAdapter;
    private ToExerciseAdapter mToAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private Boolean mOnRoutine = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        if(Parcels.unwrap(getIntent().getParcelableExtra("exercises")) != null){
            mExercisesTo = Parcels.unwrap(getIntent().getParcelableExtra("exercises"));
        }

        initializeSearch();

        mSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mSource.isChecked()){
                    mSource.setText("Exercises");
                    mOnRoutine = false;
                    mSearch.setQuery("", false);
                    mFromStartAdapter.toggleDataset();
                }else{
                    mSource.setText("Routines");
                    mOnRoutine = true;
                    mSearch.setQuery("", false);
                    mFromStartAdapter.toggleDataset();
                }
            }
        });

        mFromStartAdapter = new FromStartAdapter(getApplicationContext(), this, mSearchArrayRoutines, mSearchArrayRoutineNames, mSearchArrayExercises,mSearchArrayExcerciseNames);
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
        mSave.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v == mDone){
            if(validateSelected(mExercisesTo) && validateFields(mExercisesTo)){
                Toast.makeText(StartActivity.this, "Workout completed", Toast.LENGTH_SHORT).show();

                Workout workout = new Workout(mExercisesTo);
                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_WORKOUTS).push();
                workout.setPushId(pushRef.getKey());
                pushRef.setValue(workout);

                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }if(v == mSave){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo) && validateName(mName.getText().toString())){
                Toast.makeText(StartActivity.this, "Routine created", Toast.LENGTH_SHORT).show();
                Routine routine = new Routine(mName.getText().toString(), mExercisesTo);

                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                routine.setPushId(pushRef.getKey());
                pushRef.setValue(routine);
            }
        }
    }

    @Override
    public void setValues(Exercise exercise){
        mExercisesTo.add(exercise);
        mToAdapter.notifyDataSetChanged();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                mSearchArrayRoutines.add(routine);

                mRoutineNames.add(routine.getName());
                mSearchArrayRoutineNames.add(routine.getName());

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
                mSearchArrayExercises.add(exercise);

                mExerciseNames.add(exercise.getName());
                mSearchArrayExcerciseNames.add(exercise.getName());

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

    private void initializeSearch(){
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mOnRoutine){
                    mSearchArrayRoutines.clear();
                    mSearchArrayRoutineNames.clear();
                    for(int i = 0; i < mRoutines.size(); i++){
                        Routine routine = mRoutines.get(i);
                        if(routine.getName().toLowerCase().contains(newText.toLowerCase())){
                            mSearchArrayRoutines.add(routine);
                            mSearchArrayRoutineNames.add(routine.getName());
                            Log.v(TAG, routine.getName());
                        }
                    }
                    mFromStartAdapter.notifyDataSetChanged();
                }else{
                    mSearchArrayExercises.clear();
                    mSearchArrayExcerciseNames.clear();
                    for(int i = 0; i < mExercises.size(); i++){
                        Exercise exercise = mExercises.get(i);
                        if(exercise.getName().toLowerCase().contains(newText.toLowerCase())){
                            mSearchArrayExercises.add(exercise);
                            mSearchArrayExcerciseNames.add(exercise.getName());
                            Log.v(TAG, exercise.getName());
                        }
                    }
                    mFromStartAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });
    }
}
