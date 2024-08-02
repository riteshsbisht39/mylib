package com.example.mylib.ui.plan;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylib.R;

import java.util.ArrayList;


public class PlanInfoAdapter extends RecyclerView.Adapter<PlanInfoAdapter.PlansViewHolder> {
    private String acknowledgementDate;
    private ArrayList<String> details;
    private SparseBooleanArray sparseBooleanArray;
    //    private View.OnClickListener listener;
    private InfoItemItemClickListener listener;
    int count = 0;

    public PlanInfoAdapter(String acknowledgementDate, ArrayList<String> data, InfoItemItemClickListener listener) {
        this.acknowledgementDate = acknowledgementDate;
        this.details = data;
        sparseBooleanArray = new SparseBooleanArray(details.size());
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_erplan_info, parent, false);
        return new PlansViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlansViewHolder holder, int position) {
        holder.txtTitle.setText(details.get(position));
        if (acknowledgementDate != null) {
            holder.checkState.setVisibility(View.GONE);
        } else {
            holder.checkState.setVisibility(View.VISIBLE);
            holder.checkState.setTag(position);
            holder.checkState.setChecked(sparseBooleanArray.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    class PlansViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        View layout;
        CheckBox checkState;

        public PlansViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            txtTitle = itemView.findViewById(R.id.list_item_erplan_info_title);
            checkState = itemView.findViewById(R.id.list_item_erplan_info_state);
            checkState.setOnClickListener(layoutClickListener);
//            checkState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    Log.d("ERPInfo", "setOnCheckedChangeListener : onCheckedChanged " + b);
//                    if (b) {
//                        count++;
//                    } else {
//                        count--;
//                    }
//                    Log.d("ERPInfo", "setOnCheckedChangeListener : true count " + count);
//                    if (count == sparseBooleanArray.size()) {
//                        listener.onInfoItemClick((View) compoundButton);
//                    }
//                }
//            });
        }

    }

    public interface InfoItemItemClickListener {
        void onInfoItemClick(boolean enabled);
    }


    View.OnClickListener layoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("ERPInfo", "layoutClickListener: onClick");
            int position = (int) view.getTag();
            sparseBooleanArray.put(position, !sparseBooleanArray.get (position));
            CheckBox ch = (CheckBox) view;
            notifyDataSetChanged();
            if (ch.isChecked()) {
                count++;
            } else {
                count--;
            }

            Log.d("ERPInfo", "setOnCheckedChangeListener : true count " + count);
            Log.d("ERPInfo", "setOnCheckedChangeListener : sparseBooleanArray size " + sparseBooleanArray.size());
            if (count == details.size()) {
                listener.onInfoItemClick(true);
            } else {
                listener.onInfoItemClick(false);
            }
        }
    };
}

