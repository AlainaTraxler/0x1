package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Workout;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.ItemTouchHelperAdapter;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 12/20/16.
 */
public class FromWorkoutAdapter extends RecyclerView.Adapter<FromWorkoutAdapter.WorkoutViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<Workout> mWorkouts = new ArrayList<>();
    private Context mContext;

    DataTransferInterface dtInterface;

    private OnStartDragListener mOnStartDragListener;

    public FromWorkoutAdapter(Context context, ArrayList<Workout> workouts, DataTransferInterface dtInterface) {
        mContext = context;
        mWorkouts = workouts;
        this.dtInterface = dtInterface;
    }

    @Override
    public FromWorkoutAdapter.WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.from_workout_list_item, parent, false);
        WorkoutViewHolder viewHolder = new WorkoutViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FromWorkoutAdapter.WorkoutViewHolder holder, int position) {
        holder.bindWorkout(mWorkouts.get(position));
    }

    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        dtInterface.setObject(mWorkouts.get(position));
        this.notifyItemRemoved(position);
        this.notifyItemInserted(position);
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.WorkoutDate) TextView mWorkoutName;
        private Context mContext;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindWorkout(Workout workout) {
            mWorkoutName.setText(workout.getCompleted());
        }
    }
}
