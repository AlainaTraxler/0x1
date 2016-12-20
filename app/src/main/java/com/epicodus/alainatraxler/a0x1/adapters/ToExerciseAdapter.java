package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.ItemTouchHelperAdapter;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;

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

        Log.v("From", fromPosition + "");
        Log.v("To", toPosition + "");

        mExercises.add(toPosition, catcher);
        if(fromPosition > toPosition){
            mExercises.remove(fromPosition + 1);
        }else{
            mExercises.remove(fromPosition - 1);
        }
        for(int i = 0; i < mExercises.size(); i++){
            Log.v(i + "", mExercises.get(i).getName());
        }
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mExercises.remove(position);
        mViewHolder.onDismiss();
        this.notifyItemRemoved(position);
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ExerciseName) TextView mExerciseName;
        @Bind(R.id.Sets) EditText mSets;
        @Bind(R.id.Reps) EditText mReps;
        @Bind(R.id.Weight) EditText mWeight;
        @Bind(R.id.Drag) ImageView mDrag;

        private Context mContext;

        TextWatcher mSetWatcher;
        TextWatcher mRepWatcher;
        TextWatcher mWeightWatcher;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindExercise(final Exercise exercise) {
            mExerciseName.setText(exercise.getName());

            mSetWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String catcher = mSets.getText().toString();
                    if(!catcher.equals("")){
                        mExercises.get(mExercises.indexOf(exercise)).setSets(Integer.parseInt(catcher));
                    }else{
                        mExercises.get(mExercises.indexOf(exercise)).setSets(0);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            mSets.addTextChangedListener(mSetWatcher);

            mRepWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String catcher = mReps.getText().toString();
                    if(!catcher.equals("")){
                        mExercises.get(mExercises.indexOf(exercise)).setReps(Integer.parseInt(catcher));
                    }else{
                        mExercises.get(mExercises.indexOf(exercise)).setReps(0);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            mReps.addTextChangedListener(mRepWatcher);

            mWeightWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String catcher = mWeight.getText().toString();
                    if(!catcher.equals("")){
                        mExercises.get(mExercises.indexOf(exercise)).setWeight(Integer.parseInt(catcher));
                    }else{
                        mExercises.get(mExercises.indexOf(exercise)).setWeight(0);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            mWeight.addTextChangedListener(mWeightWatcher);
        }

        public void onDismiss(){
            mSets.removeTextChangedListener(mSetWatcher);
            mSets.setText("");

            mReps.removeTextChangedListener(mRepWatcher);
            mReps.setText("");

            mWeight.removeTextChangedListener(mWeightWatcher);
            mWeight.setText("");
        }
    }
}
