package com.epicodus.alainatraxler.a0x1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.alainatraxler.a0x1.R;
import com.epicodus.alainatraxler.a0x1.models.Routine;
import com.epicodus.alainatraxler.a0x1.util.DataTransferInterface;
import com.epicodus.alainatraxler.a0x1.util.ItemTouchHelperAdapter;
import com.epicodus.alainatraxler.a0x1.util.OnStartDragListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 12/20/16.
 */
public class FromRoutineAdapter extends RecyclerView.Adapter<FromRoutineAdapter.RoutineViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<Routine> mRoutines = new ArrayList<>();
    private ArrayList<Routine> mRoutinesTo = new ArrayList<>();
    private Context mContext;

    DataTransferInterface dtInterface;

    private OnStartDragListener mOnStartDragListener;

    public FromRoutineAdapter(Context context, ArrayList<Routine> exercises, DataTransferInterface dtInterface) {
        mContext = context;
        mRoutines = exercises;
        this.dtInterface = dtInterface;
    }

    @Override
    public FromRoutineAdapter.RoutineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.from_routine_list_item, parent, false);
        RoutineViewHolder viewHolder = new RoutineViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FromRoutineAdapter.RoutineViewHolder holder, int position) {
        holder.bindRoutine(mRoutines.get(position));
    }

    @Override
    public int getItemCount() {
        return mRoutines.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        dtInterface.setObject(mRoutines.get(position));
        this.notifyItemRemoved(position);
        this.notifyItemInserted(position);
    }

    public class RoutineViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.RoutineName)
        TextView mRoutineName;
        private Context mContext;

        public RoutineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindRoutine(Routine routine) {
            mRoutineName.setText(routine.getName());
        }
    }
}
