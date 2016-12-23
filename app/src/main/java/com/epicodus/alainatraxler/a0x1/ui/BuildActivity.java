package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.ToExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.models.Workout;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;
import com.epicodus.alainatraxler.a0x1.util.SimpleItemTouchHelperCallback;
import com.google.firebase.auth.api.model.StringList;
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

public class BuildActivity extends BaseActivity implements DataTransferInterface, View.OnClickListener, OnStartDragListener {
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;
    @Bind(R.id.Save) Button mSave;
    @Bind(R.id.Do) Button mDo;
    @Bind(R.id.Name) EditText mName;
    @Bind(R.id.Search) SearchView mSearch;

    private FromExerciseAdapter mFromAdapter;
    private ToExerciseAdapter mToAdapter;
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private ArrayList<Exercise> mSearchArray = new ArrayList<Exercise>();
    private ItemTouchHelper mItemTouchHelper;

    private String previousName = "";
    private Boolean mInUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);
        ButterKnife.bind(this);

        initializeSearch();

        mInUpdate = getIntent().getBooleanExtra("inUpdate", false);

        if(mInUpdate){
            mSave.setText("Update");
            mSave.setBackgroundColor(Color.parseColor("#66BB6A"));
            mName.setText(getIntent().getStringExtra("routineName"));
            mExercisesTo = Parcels.unwrap(getIntent().getParcelableExtra("exercises"));
        }

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

        mFromAdapter = new FromExerciseAdapter(getApplicationContext(), mSearchArray, this);
        mRecyclerViewFrom.setAdapter(mFromAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(BuildActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mToAdapter = new ToExerciseAdapter(getApplicationContext(), mExercisesTo, this, this, mRecyclerViewTo);
        mRecyclerViewTo.setAdapter(mToAdapter);
        RecyclerView.LayoutManager ToLayoutManager =
                new LinearLayoutManager(BuildActivity.this);
        mRecyclerViewTo.setLayoutManager(ToLayoutManager);
        mRecyclerViewTo.setHasFixedSize(true);

        mToAdapter.setReference(mExercises);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        getExercises();

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        ItemTouchHelper.Callback callbackTo = new SimpleItemTouchHelperCallback(mToAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackTo);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewTo);

        mSave.setOnClickListener(this);
        mDo.setOnClickListener(this);

//        overrideFonts(mContext, findViewById(android.R.id.content), Constants.FONT_MAIN);
    }

    @Override
    public void onClick(View v){
        if(v == mSave){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo) && validateName(mName.getText().toString())){
                if(mInUpdate){
                    Toast.makeText(BuildActivity.this, "Routine updated", Toast.LENGTH_SHORT).show();

                    String currentPushId = getIntent().getStringExtra("currentPushId");

                    dbCurrentUser.child(Constants.DB_NODE_ROUTINES).child(currentPushId).child("name").setValue(mName.getText().toString());
                    dbCurrentUser.child(Constants.DB_NODE_ROUTINES).child(currentPushId).child(Constants.DB_NODE_EXERCISES).setValue(mExercisesTo);
                }else{
                    Toast.makeText(BuildActivity.this, "Routine created", Toast.LENGTH_SHORT).show();
                    Routine routine = new Routine(mName.getText().toString(), mExercisesTo);

                    DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                    routine.setPushId(pushRef.getKey());
                    pushRef.setValue(routine);
                }
                Intent intent = new Intent(BuildActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }if(v == mDo){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo)){
                Intent intent = new Intent(BuildActivity.this, StartActivity.class);
                intent.putExtra("exercises", Parcels.wrap(mExercisesTo));
                startActivity(intent);
            }
        }
    }

    @Override
    public void setValues(Exercise exercise) {
        mExercisesTo.add(exercise.clone(exercise));
        Log.v(">>>> Position", mExercisesTo.indexOf(exercise) + "");
        mToAdapter.notifyItemInserted(mExercisesTo.size()   );

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    public void setRoutine(Routine routine){}

    @Override
    public void setString(String string){}

    @Override
    public void setObject(Object object){}

    public void getExercises(){
        dbExercises.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mExercises.clear();
                mSearchArray.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    mExercises.add(exercise);
                    mSearchArray.add(exercise);
                }

                mFromAdapter.notifyDataSetChanged();
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
                mSearchArray.clear();
                for(int i = 0; i < mExercises.size(); i++){
                    Exercise exercise = mExercises.get(i);
                    if(exercise.getName().toLowerCase().contains(newText.toLowerCase())){
                        mSearchArray.add(exercise);
                        Log.v(TAG, exercise.getName());
                    }
                }
                mFromAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
