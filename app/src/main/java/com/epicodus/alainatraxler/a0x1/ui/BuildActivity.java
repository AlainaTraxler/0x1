package com.epicodus.alainatraxler.a0x1.ui;

import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);
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
                    if(currentName.contains("\r") || currentName.contains("\n") || currentName.charAt(0) == ' '){
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

        mSave.setOnClickListener(this);
        mDo.setOnClickListener(this);

//        overrideFonts(mContext, findViewById(android.R.id.content), Constants.FONT_MAIN);
    }

    @Override
    public void onClick(View v){
        if(v == mSave){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo) && validateName(mName.getText().toString())){
                Toast.makeText(BuildActivity.this, "Routine created", Toast.LENGTH_SHORT).show();
                Routine routine = new Routine(mName.getText().toString(), mExercisesTo);

                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                routine.setPushId(pushRef.getKey());
                pushRef.setValue(routine);

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
        mExercisesTo.add(exercise);
        mToAdapter.notifyDataSetChanged();

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
        dbExercises.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mExercises.add(dataSnapshot.getValue(Exercise.class));
                mSearchArray.add(dataSnapshot.getValue(Exercise.class));
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
