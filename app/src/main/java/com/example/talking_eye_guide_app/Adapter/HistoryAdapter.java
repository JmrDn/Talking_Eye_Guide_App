package com.example.talking_eye_guide_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talking_eye_guide_app.Model.HistoryModel;
import com.example.talking_eye_guide_app.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<HistoryModel> list;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> list){
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        HistoryModel model = list.get(position);
        holder.locationTV.setText(model.getLocation());
        holder.dateTV.setText(model.getDate());
        holder.timeTV.setText(model.getTime());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV, timeTV, locationTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.day_Textview);
            timeTV = itemView.findViewById(R.id.time_Textview);
            locationTV = itemView.findViewById(R.id.location_Textview);
        }
    }
}
