package com.vshetty.mac;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MineAdapter extends RecyclerView.Adapter<MineAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";
    static Context context;


    ArrayList<WaterSource> waterList;

    public MineAdapter(Context context, ArrayList<WaterSource> waterList) {
        this.context = context;
        this.waterList = waterList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mywatersource,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.position = position;
        WaterSource waterSource = waterList.get(position);
        holder.waterSource = waterSource;
        holder.name.setText(waterSource.getName());
        String k = "Offline";
        if(waterSource.status == true)
            k = "Online";
        holder.status.setText(k);
    }

    @Override
    public int getItemCount() {
        return waterList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,status;
        int position;
        WaterSource waterSource;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.watersourceName);
            status = itemView.findViewById(R.id.tvStatus);

            itemView.findViewById(R.id.monitorBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!waterSource.status){
                        Toast.makeText(context, "Water Source is Offline!!!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // start new activity that is monitoring
                        static_behaviour.water = waterSource.getName();
                        Intent intent = new Intent(context, mainPage.class);
                        context.startActivity(intent);
                    }
                }
            });
        }


    }
}
