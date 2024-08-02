package com.example.mylib.ui.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylib.R;
import com.example.mylib.api.plan.ERPlansResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlansViewHolder> {
    private final int imageSize;
    private List<ERPlansResponse> plans;
    private View.OnClickListener listener;
    private Context context;

    public PlansAdapter(Context context, ArrayList<ERPlansResponse> data, View.OnClickListener listener) {
        this.context = context;
        this.plans = data;
        imageSize = (int) (context.getResources().getDisplayMetrics().widthPixels * .07);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_erplans, parent, false);
        return new PlansViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlansViewHolder holder, int position) {
        ERPlansResponse erPlan = plans.get(position);
        holder.txtTitle.setText(erPlan.title);
        Picasso.with(context).load(erPlan.icon).resize(imageSize, imageSize)
                .centerCrop()
                .into(holder.image);
        holder.layout.setTag(erPlan);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    class PlansViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        ImageView image;
        View layout;

        public PlansViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.list_item_erplans_title);
            image = itemView.findViewById(R.id.list_item_erplans_img);
            layout = itemView;
            itemView.setOnClickListener(listener);
        }
    }
}

