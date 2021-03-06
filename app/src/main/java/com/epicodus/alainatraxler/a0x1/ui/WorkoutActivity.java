package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
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
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

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
    @Bind(R.id.Search) SearchView mSearch;

    private ArrayList<Workout> mWorkouts = new ArrayList<Workout>();
    private ArrayList<Workout> mSearchArray = new ArrayList<Workout>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private FromWorkoutAdapter mFromWorkoutAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ToExerciseAdapter mToAdapter;

    private String currentPushId;
    private String previousName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);

        initializeSearch();

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentName = mName.getText().toString();
                if(currentName.length() > 0){
                    if(currentName.contains("\r") || currentName.contains("\n") || currentName.charAt(0) == ' ' || currentName.length() > 20){
                        mName.setText(previousName);
                        mName.setSelection(previousName.length());
                    }else{
                        previousName = currentName;
                    }
                }else{
                    previousName = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mFromWorkoutAdapter = new FromWorkoutAdapter(getApplicationContext(), mSearchArray, this);
        mRecyclerViewFrom.setAdapter(mFromWorkoutAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(WorkoutActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mToAdapter = new ToExerciseAdapter(getApplicationContext(), mExercisesTo, this, this, mRecyclerViewTo);
        mRecyclerViewTo.setAdapter(mToAdapter);
        RecyclerView.LayoutManager ToLayoutManager =
                new LinearLayoutManager(WorkoutActivity.this);
        mRecyclerViewTo.setLayoutManager(ToLayoutManager);
        mRecyclerViewTo.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromWorkoutAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        ItemTouchHelper.Callback callbackTo = new SimpleItemTouchHelperCallback(mToAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackTo);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewTo);

        getWorkouts();

        mSave.setOnClickListener(this);
        mDo.setOnClickListener(this);
        mUpdate.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == mSave){
            if(validateSelected(mExercisesTo) && validateFields(mExercisesTo) && validateName(mName.getText().toString())){
                Toast.makeText(WorkoutActivity.this, "New routine created", Toast.LENGTH_SHORT).show();

                Routine routine = new Routine(mName.getText().toString(), mExercisesTo);
                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                routine.setPushId(pushRef.getKey());
                pushRef.setValue(routine);
            }
        }else if(v == mDo){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo)){
                Intent intent = new Intent(WorkoutActivity.this, StartActivity.class);
                intent.putExtra("exercises", Parcels.wrap(mExercisesTo));
                startActivity(intent);
            }
        }else if(v == mDelete){
            if(validateSelected(mExercisesTo)){
                if(currentPushId != null){
                    Toast.makeText(WorkoutActivity.this, "Workout deleted", Toast.LENGTH_SHORT).show();
                    dbCurrentUser.child(Constants.DB_NODE_WORKOUTS).child(currentPushId).removeValue();

                    int catcher = mExercisesTo.size();
                    mToAdapter.resetExercises();
                    mExercisesTo.clear();
                    mToAdapter.notifyItemRangeRemoved(0, catcher);

                    mSearch.setQuery("", false);
                    getWorkouts();
                }
            }
        }else if(v == mUpdate){
            if(validateSelected(mExercisesTo) && validateFields(mExercisesTo)){
                Intent intent = new Intent(WorkoutActivity.this, StartActivity.class);
                intent.putExtra("exercises", Parcels.wrap(mExercisesTo));
                intent.putExtra("inUpdate", true);
                intent.putExtra("currentPushId", currentPushId);
                startActivity(intent);
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
        Log.v(TAG, "PushID: " + currentPushId);

        int catcher = mExercisesTo.size();
        mToAdapter.resetExercises();
        mExercisesTo.clear();
        mToAdapter.notifyItemRangeRemoved(0, catcher);

        for(int i = 0; i < workout.getExercises().size(); i++){
            mExercisesTo.add(workout.getExercises().get(i));
        }
        mToAdapter.notifyDataSetChanged();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void getWorkouts(){
        Log.v(TAG, "Getting workouts");

        dbCurrentUser.child(Constants.DB_NODE_WORKOUTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mWorkouts.clear();
                mSearchArray.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Workout workout = snapshot.getValue(Workout.class);
                    mWorkouts.add(workout);
                    mSearchArray.add(workout);
                }

                mFromWorkoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeSearch(){
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchArray.clear();
                for(int i = 0; i < mWorkouts.size(); i++){
                    Workout workout = mWorkouts.get(i);
                    if(workout.getCompleted().toLowerCase().contains(newText.toLowerCase())){
                        mSearchArray.add(workout);
                    }
                }
                mFromWorkoutAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
