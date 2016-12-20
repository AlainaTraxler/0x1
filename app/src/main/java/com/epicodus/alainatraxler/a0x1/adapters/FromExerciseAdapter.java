package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 12/19/16.
 */
public class FromExerciseAdapter extends RecyclerView.Adapter<FromExerciseAdapter.ExerciseViewHolder> {
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private Context mContext;

    public FromExerciseAdapter(Context context, ArrayList<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }

    @Override
    public FromExerciseAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.from_exercise_list_item, parent, false);
        ExerciseViewHolder viewHolder = new ExerciseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FromExerciseAdapter.ExerciseViewHolder holder, int position) {
        holder.bindExercise(mExercises.get(position));
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ExerciseName) TextView mExerciseName;
        private Context mContext;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindExercise(Exercise exercise) {
            mExerciseName.setText(exercise.getName());
        }
    }
}
