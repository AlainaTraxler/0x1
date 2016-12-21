package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Exercise;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.ItemTouchHelperAdapter;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 12/20/16.
 */
public class FromStartAdapter extends RecyclerView.Adapter<FromStartAdapter.NameViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<Routine> mRoutines = new ArrayList<>();
    private ArrayList<String> mRoutineNames = new ArrayList<>();
    private ArrayList<String> mExerciseNames = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private Context mContext;
    private Boolean onRoutine = true;

    DataTransferInterface dtInterface;

    private OnStartDragListener mOnStartDragListener;

    public FromStartAdapter(Context context, DataTransferInterface dtInterface, ArrayList<Routine> routines, ArrayList<String> routineNames, ArrayList<Exercise> exercises, ArrayList<String> exerciseNames) {
        mContext = context;
        mExercises = exercises;
        mRoutines = routines;
        mRoutineNames = routineNames;
        mExerciseNames = exerciseNames;
        mNames = routineNames;
        this.dtInterface = dtInterface;
    }

    @Override
    public FromStartAdapter.NameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.from_start_list_item, parent, false);
        NameViewHolder viewHolder = new NameViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FromStartAdapter.NameViewHolder holder, int position) {
        holder.bindName(mNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
//        dtInterface.setString(mNames.get(position));

        if(onRoutine){
            Routine routine = mRoutines.get(position);
            for(int i = 0; i < routine.getExercises().size(); i++){
                dtInterface.setValues(routine.getExercises().get(i));
            }
        }else{
            dtInterface.setValues(mExercises.get(position));
        }
        this.notifyItemRemoved(position);
        this.notifyItemInserted(position);
    }

    public void toggleDataset(){
        if(onRoutine){
            onRoutine = false;
            mNames = mExerciseNames;
            Log.v("Toggle", "Exercise");
            this.notifyDataSetChanged();
        }else{
            onRoutine = true;
            mNames = mRoutineNames;
            Log.v("Toggle", "Routine");
            this.notifyDataSetChanged();
        }
    }

    public class NameViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.Name) TextView mName;
        private Context mContext;

        public NameViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindName(String name) {
            mName.setText(name);
        }
    }
}
