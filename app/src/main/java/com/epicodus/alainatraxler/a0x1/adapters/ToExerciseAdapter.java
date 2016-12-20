package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    DataTransferInterface dtInterface;

    private OnStartDragListener mOnStartDragListener;

    public ToExerciseAdapter(Context context, ArrayList<Exercise> exercises, DataTransferInterface dtInterface) {
        mContext = context;
        mExercises = exercises;
        this.dtInterface = dtInterface;
    }

    @Override
    public ToExerciseAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_exercise_list_item, parent, false);
        ExerciseViewHolder viewHolder = new ExerciseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ToExerciseAdapter.ExerciseViewHolder holder, int position) {
        holder.bindExercise(mExercises.get(position));
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mExercises.remove(position);
        this.notifyItemRemoved(position);
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ExerciseName)
        TextView mExerciseName;
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
