package com.vshetty.mac;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";
    static Context context;
    DatabaseReference databaseReference;

    Button btn;

    ArrayList<WaterSource> waterList;

    public MyAdapter(Context context, ArrayList<WaterSource> waterList) {
        this.context = context;
        this.waterList = waterList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.watersource,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.position = position;
        databaseReference = FirebaseDatabase.getInstance("https://realtimewqms-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        WaterSource waterSource = waterList.get(position);
        holder.waterSource = waterSource;
        holder.name.setText(waterSource.getName());
        String k = "Offline";
        if(waterSource.status == true) {
            k = "Online";
        }
        holder.status.setText(k);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String mail = firebaseUser.getEmail();
        char[] charArray = mail.toCharArray();
        int i = 0;
        String firstName = "";
        while(charArray[i]!='@'){
            firstName += charArray[i];
            i++;
        }


        databaseReference.child(firstName).child("select_ws").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot:task.getResult().getChildren()){
                    if(snapshot.getKey().equals(waterSource.name)){
                        Button btn = holder.itemView.findViewById(R.id.addBtn);
                        btn.setText("Remove");
                        btn.setBackgroundColor(Color.RED);
                    }
                }
            }
        });

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

            itemView.findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d(TAG, "onClick: " + waterSource.name);
//
//                    if(button.getText().equals("Yabbo")){
//                        button.setText("Add");
//                        button.setBackgroundColor(Color.BLACK);
//                    }
//                    else {
//                        button.setText("Yabbo");
//                        button.setBackgroundColor(Color.RED);
//                    }
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String mail = firebaseUser.getEmail();
                    char[] charArray = mail.toCharArray();
                    int i = 0;
                    String firstName = "";
                    while(charArray[i]!='@'){
                        firstName += charArray[i];
                        i++;
                    }
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance("https://realtimewqms-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
                    Button button = v.findViewById(v.getId());
                    if(button.getText().equals("Add")){
                        databaseReference1.child(firstName).child("select_ws").child(waterSource.name).setValue("");
                        button.setText("Remove");
                        button.setBackgroundColor(Color.RED);
                        Toast.makeText(context, waterSource.name + " added successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        databaseReference1.child(firstName).child("select_ws").child(waterSource.name).removeValue();
                        button.setText("Add");
                        button.setBackgroundColor(Color.BLUE);
                    }

                }
            });

        }


    }
}
