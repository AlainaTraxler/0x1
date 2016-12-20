package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.ToExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class BuildActivity extends BaseActivity implements DataTransferInterface, View.OnClickListener, OnStartDragListener {
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;
    @Bind(R.id.LetsGo) Button mLetsGo;
    @Bind(R.id.Name) EditText mName;

    private FromExerciseAdapter mFromAdapter;
    private ToExerciseAdapter mToAdapter;
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);
        ButterKnife.bind(this);

        mFromAdapter = new FromExerciseAdapter(getApplicationContext(), mExercises, this);
        mRecyclerViewFrom.setAdapter(mFromAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(BuildActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mToAdapter = new ToExerciseAdapter(getApplicationContext(), mExercisesTo, this, this);
        mRecyclerViewTo.setAdapter(mToAdapter);
        RecyclerView.LayoutManager ToLayoutManager =
                new LinearLayoutManager(BuildActivity.this);
        mRecyclerViewTo.setLayoutManager(ToLayoutManager);
        mRecyclerViewTo.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        getExercises();

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        ItemTouchHelper.Callback callbackTo = new SimpleItemTouchHelperCallback(mToAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackTo);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewTo);

        mLetsGo.setOnClickListener(this);

//        overrideFonts(mContext, findViewById(android.R.id.content), Constants.FONT_MAIN);
    }

    @Override
    public void onClick(View v){
        if(v == mLetsGo){
            if(validate()){
                Toast.makeText(BuildActivity.this, "Routine created!", Toast.LENGTH_SHORT).show();
                Routine routine = new Routine(mName.getText().toString(), mExercisesTo);

                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                routine.setPushId(pushRef.getKey());
                pushRef.setValue(routine);

                Intent intent = new Intent(BuildActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public Boolean validate(){
        if(mExercisesTo.size() == 0){
            Toast.makeText(BuildActivity.this, "You haven't selected anything!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mName.getText().toString().equals("")){
            Toast.makeText(BuildActivity.this, "You need to name your new routine!", Toast.LENGTH_SHORT).show();
            return false;
        }

        for(int i = 0; i < mExercisesTo.size(); i++){
            Exercise exercise = mExercisesTo.get(i);
            if(exercise.getType().equals(Constants.TYPE_WEIGHT)){
                if(exercise.getSets() <= 0 || exercise.getReps() <= 0 || exercise.getWeight() <= 0){
                    Toast.makeText(BuildActivity.this, "Something's wrong! Make sure all fields are filled out.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                if(exercise.getTime() <= 0 || exercise.getDistance() <= 0){
                    Toast.makeText(BuildActivity.this, "Something's wrong! Make sure all fields are filled out.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void setValues(Exercise exercise) {

        mExercisesTo.add(exercise);
        mToAdapter.notifyDataSetChanged();
    }

    @Override
    public void setRoutine(Routine routine){}

    public void getExercises(){
        dbExercises.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mExercises.add(dataSnapshot.getValue(Exercise.class));
                mFromAdapter.notifyDataSetChanged();
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
}
