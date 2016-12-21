package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.provider.Contacts;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.Constants;
import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.DoubleFilterMinMax;
import com.epicodus.alainatraxler.a0x1.util.InputFilterMinMax;
import com.epicodus.alainatraxler.a0x1.util.ItemTouchHelperAdapter;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 12/19/16.
 */
public class ToExerciseAdapter extends RecyclerView.Adapter<ToExerciseAdapter.ExerciseViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private Context mContext;

    private ExerciseViewHolder mViewHolder;

    DataTransferInterface dtInterface;

    private OnStartDragListener mOnStartDragListener;

    public ToExerciseAdapter(Context context, ArrayList<Exercise> exercises, DataTransferInterface dtInterface, OnStartDragListener onStartDragListener) {
        mContext = context;
        mExercises = exercises;
        this.dtInterface = dtInterface;
        mOnStartDragListener = onStartDragListener;
    }

    @Override
    public ToExerciseAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_exercise_list_item, parent, false);
        ExerciseViewHolder viewHolder = new ExerciseViewHolder(view);
        mViewHolder = viewHolder;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ToExerciseAdapter.ExerciseViewHolder holder, int position) {
        holder.bindExercise(mExercises.get(position));
        holder.mDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        Exercise catcher = mExercises.get(fromPosition);

        if(fromPosition > toPosition){
            mExercises.add(toPosition, catcher);
            mExercises.remove(fromPosition + 1);
        }else{
            mExercises.add(toPosition + 1, catcher);
            mExercises.remove(fromPosition);
        }

        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mExercises.remove(position);
        this.notifyItemRemoved(position);
    }

    public void resetExercises(){
        mExercises.clear();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.ExerciseName) TextView mExerciseName;
        @Bind(R.id.At) TextView mAt;
        @Bind(R.id.X) TextView mX;
        @Bind(R.id.Ex) TextView mEx;
        @Bind(R.id.Sets) EditText mSets;
        @Bind(R.id.Reps) EditText mReps;
        @Bind(R.id.Weight) EditText mWeight;
        @Bind(R.id.Time) EditText mTime;
        @Bind(R.id.Distance) EditText mDistance;
        @Bind(R.id.Drag) ImageView mDrag;

        private Context mContext;
        private View mItemView;

        private TextWatcher mSetWatcher;
        private TextWatcher mRepWatcher;
        private TextWatcher mWeightWatcher;
        private TextWatcher mTimeWatcher;
        private TextWatcher mDistanceWatcher;

        private Exercise mExercise;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            mItemView = itemView;
        }

        private void disableWeight(){
            mWeight.setVisibility(View.INVISIBLE);
            mAt.setVisibility(View.INVISIBLE);
        }

        private void disableTimeandDistance(){
            mEx.setVisibility(View.INVISIBLE);
            mTime.setVisibility(View.INVISIBLE);
            mDistance.setVisibility(View.INVISIBLE);
        }

        private void disableRepsAndSets(){
            mReps.setVisibility(View.INVISIBLE);
            mSets.setVisibility(View.INVISIBLE);
            mX.setVisibility(View.INVISIBLE);
        }

        private void enableViews(){
            mReps.setVisibility(View.VISIBLE);
            mSets.setVisibility(View.VISIBLE);
            mX.setVisibility(View.VISIBLE);
            mEx.setVisibility(View.VISIBLE);
            mTime.setVisibility(View.VISIBLE);
            mDistance.setVisibility(View.VISIBLE);
            mWeight.setVisibility(View.VISIBLE);
            mAt.setVisibility(View.VISIBLE);
        }

        public void bindExercise(final Exercise exercise) {
            if(exercise.getType().equals(Constants.TYPE_BODYWEIGHT)){
                enableViews();
                disableWeight();
                disableTimeandDistance();
            }else if(exercise.getType().equals(Constants.TYPE_WEIGHT)){
                enableViews();
                disableTimeandDistance();
            }else if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                enableViews();
                disableWeight();
                disableRepsAndSets();
            }

            mExercise = exercise;
            mExerciseName.setText(exercise.getName());

            if(exercise.getSets() != null){
                mSets.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "1000")});

                mSetWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mSets.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setSets(Integer.parseInt(catcher));
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setSets(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mSets.addTextChangedListener(mSetWatcher);

                if(exercise.getSets() != 0){
                    mSets.setText(String.valueOf(exercise.getSets()));
                }
            }

            if(exercise.getReps() != null){
                mReps.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "1000")});

                mRepWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mReps.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setReps(Integer.parseInt(catcher));
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setReps(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mReps.addTextChangedListener(mRepWatcher);

                if(exercise.getReps() != 0){
                    mReps.setText(String.valueOf(exercise.getReps()));
                }
            }

            if(exercise.getWeight() != null){
                mWeight.setFilters(new InputFilter[]{ new DoubleFilterMinMax("1", "1000")});

                mWeightWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mWeight.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            Log.v("Before:", catcher);
                            Double formattedWeight = Double.parseDouble(String.format("%.2f", Double.parseDouble(catcher)));
                            Log.v("After:", String.valueOf(formattedWeight));
                            mExercises.get(mExercises.indexOf(exercise)).setWeight(formattedWeight);
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setWeight(0.0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mWeight.addTextChangedListener(mWeightWatcher);

                if(exercise.getWeight() != 0){
                    mWeight.setText(String.valueOf(exercise.getWeight()));
                }
            }

            if(exercise.getTime() != null){
                mTimeWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mTime.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setTime(catcher);
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setTime("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mTime.addTextChangedListener(mTimeWatcher);

                if(exercise.getTime().length() != 0 && !(mExercises.get(mExercises.indexOf(exercise)).getTime().equals("0:00"))){
                    mTime.setText(String.valueOf(exercise.getTime()));
                }
            }

            if(exercise.getDistance() != null){
                mDistance.setFilters(new InputFilter[]{ new DoubleFilterMinMax("1", "1000")});

                mDistanceWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mDistance.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setDistance(Double.parseDouble(catcher));
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setDistance(0.0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mDistance.addTextChangedListener(mDistanceWatcher);

                if(exercise.getDistance() != 0){
                    mDistance.setText(String.valueOf(exercise.getDistance()));
                }
            }
        }
    }
}
