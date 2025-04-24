package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecentLocationsAdapter extends RecyclerView.Adapter<RecentLocationsAdapter.ViewHolder> {

    private ArrayList<String> recentLocations;
    private Context context;
    private OnLocationClickListener listener;

    // Listener-rajapinta klikkauksen käsittelyyn
    public interface OnLocationClickListener {
        void onLocationClick(String location);
    }

    public RecentLocationsAdapter(Context context, ArrayList<String> recentLocations, OnLocationClickListener listener) {
        this.context = context;
        this.recentLocations = recentLocations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each row
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the TextView
        String location = recentLocations.get(position);
        holder.textViewLocationName.setText(location);

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLocationClick(location); // Kutsu listeneria
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentLocations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLocationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLocationName = itemView.findViewById(R.id.textViewLocationName);
        }
    }
}