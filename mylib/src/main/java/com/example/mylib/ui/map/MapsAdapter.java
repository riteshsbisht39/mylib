package com.example.mylib.ui.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylib.R;
import com.example.mylib.api.map.MapResponse;

import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MapViewHolder> {

    private List<MapResponse> mapsList;
    private OnItemClickedListener listener;

    public MapsAdapter(List<MapResponse> mapsList, OnItemClickedListener listener) {
        this.mapsList = mapsList;
        this.listener = listener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(MapResponse map);
    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_maps, parent, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder holder, int position) {
        MapResponse map = mapsList.get(position);
        holder.textMapName.setText(map.getName());

        holder.textMapName.setOnClickListener(v ->
                listener.onItemClicked(map));
    }

    @Override
    public int getItemCount() {
        return mapsList.size();
    }

    class MapViewHolder extends RecyclerView.ViewHolder {

        private TextView textMapName;

        MapViewHolder(@NonNull View itemView) {
            super(itemView);
            textMapName = itemView.findViewById(R.id.list_map_name);
        }
    }
}
