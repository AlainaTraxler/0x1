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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.adapters.FromRoutineAdapter;
import com.epicodus.alainatraxler.a0x1.adapters.ToExerciseAdapter;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
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

public class RoutineActivity extends BaseActivity implements DataTransferInterface,OnStartDragListener,View.OnClickListener{
    @Bind(R.id.recyclerViewFrom) RecyclerView mRecyclerViewFrom;
    @Bind(R.id.recyclerViewTo) RecyclerView mRecyclerViewTo;
    @Bind(R.id.Save) Button mSave;
    @Bind(R.id.Do) Button mDo;
    @Bind(R.id.Update) Button mUpdate;
    @Bind(R.id.Delete) Button mDelete;
    @Bind(R.id.Name) EditText mName;
    @Bind(R.id.Search) SearchView mSearch;

    private ArrayList<Routine> mRoutines = new ArrayList<Routine>();
    private ArrayList<Routine> mSearchArray = new ArrayList<Routine>();
    private ArrayList<Exercise> mExercisesTo = new ArrayList<Exercise>();
    private FromRoutineAdapter mFromRoutineAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ToExerciseAdapter mToAdapter;

    private String currentPushId;
    private String previousName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);
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

        mFromRoutineAdapter = new FromRoutineAdapter(getApplicationContext(), mSearchArray, this);
        mRecyclerViewFrom.setAdapter(mFromRoutineAdapter);
        RecyclerView.LayoutManager FromLayoutManager =
                new LinearLayoutManager(RoutineActivity.this);
        mRecyclerViewFrom.setLayoutManager(FromLayoutManager);
        mRecyclerViewFrom.setHasFixedSize(true);

        mToAdapter = new ToExerciseAdapter(getApplicationContext(), mExercisesTo, this, this);
        mRecyclerViewTo.setAdapter(mToAdapter);
        RecyclerView.LayoutManager ToLayoutManager =
                new LinearLayoutManager(RoutineActivity.this);
        mRecyclerViewTo.setLayoutManager(ToLayoutManager);
        mRecyclerViewTo.setHasFixedSize(true);

        mRecyclerViewFrom.setItemAnimator(new SlideInLeftAnimator());

        ItemTouchHelper.Callback callbackFrom = new SimpleItemTouchHelperCallback(mFromRoutineAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackFrom);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewFrom);

        ItemTouchHelper.Callback callbackTo = new SimpleItemTouchHelperCallback(mToAdapter);
        mItemTouchHelper = new ItemTouchHelper(callbackTo);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewTo);

        getRoutines();

        mSave.setOnClickListener(this);
        mDo.setOnClickListener(this);
        mUpdate.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == mSave){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo) && validateName(mName.getText().toString())){
                Toast.makeText(RoutineActivity.this, "New routine created", Toast.LENGTH_SHORT).show();

                Routine routine = new Routine(mName.getText().toString(), mExercisesTo);
                DatabaseReference pushRef = dbCurrentUser.child(Constants.DB_NODE_ROUTINES).push();
                routine.setPushId(pushRef.getKey());
                pushRef.setValue(routine);

                getRoutines();
            }
        }else if(v == mDo){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo)){
                Intent intent = new Intent(RoutineActivity.this, StartActivity.class);
                intent.putExtra("exercises", Parcels.wrap(mExercisesTo));
                startActivity(intent);
            }
        }else if(v == mDelete){
            if(validateSelected(mExercisesTo)){
                if(currentPushId != null){
                    Toast.makeText(RoutineActivity.this, "Routine deleted", Toast.LENGTH_SHORT).show();
                    dbCurrentUser.child(Constants.DB_NODE_ROUTINES).child(currentPushId).removeValue();

                    int catcher = mExercisesTo.size();
                    mToAdapter.resetExercises();
                    mExercisesTo.clear();
                    mToAdapter.notifyItemRangeRemoved(0, catcher);

                    mName.setText("");
                    mSearch.setQuery("", false);
                    getRoutines();
                }
            }
        }else if(v == mUpdate){
            if(validateSelected(mExercisesTo) && validateFieldsAllowEmpty(mExercisesTo) && validateName(mName.getText().toString())){
                Toast.makeText(RoutineActivity.this, "Routine updated", Toast.LENGTH_SHORT).show();

                dbCurrentUser.child(Constants.DB_NODE_ROUTINES).child(currentPushId).child("name").setValue(mName.getText().toString());
                dbCurrentUser.child(Constants.DB_NODE_ROUTINES).child(currentPushId).child(Constants.DB_NODE_EXERCISES).setValue(mExercisesTo);

                getRoutines();
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
        Routine routine = (Routine) object;
        currentPushId = routine.getPushId();
        Log.v(TAG, "PushID: " + currentPushId);

        int catcher = mExercisesTo.size();
        mToAdapter.resetExercises();
        mExercisesTo.clear();
        mToAdapter.notifyItemRangeRemoved(0, catcher);

        mName.setText(routine.getName());

        for(int i = 0; i < routine.getExercises().size(); i++){
            mExercisesTo.add(routine.getExercises().get(i));
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

    public void getRoutines(){
        Log.v(TAG, "Getting routines");
        dbCurrentUser.child(Constants.DB_NODE_ROUTINES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRoutines.clear();
                mSearchArray.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Routine routine = snapshot.getValue(Routine.class);
                    mRoutines.add(routine);
                    mSearchArray.add(routine);
                }

                mFromRoutineAdapter.notifyDataSetChanged();
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
                for(int i = 0; i < mRoutines.size(); i++){
                    Routine routine = mRoutines.get(i);
                    if(routine.getName().toLowerCase().contains(newText.toLowerCase())){
                        mSearchArray.add(routine);
                    }
                }
                mFromRoutineAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
