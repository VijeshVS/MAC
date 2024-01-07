package com.vshetty.mac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class watersourceList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView welcomeMsg;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;

    ProgressBar progressBar;

    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<WaterSource> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watersource_list);

        drawerLayout = findViewById(R.id.drawerLayout_home);
        navigationView = findViewById(R.id.navigationView_home);
        toolbar = findViewById(R.id.toolbar_home);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigration_open,R.string.navigration_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        progressBar = findViewById(R.id.progressBarID);
        recyclerView = findViewById(R.id.watersourceList);
        databaseReference = FirebaseDatabase.getInstance("https://realtimewqms-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water_sources");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    // returns all children

//                    Toast.makeText(watersourceList.this, "name is "+name, Toast.LENGTH_SHORT).show();

                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    String status = String.valueOf(dataSnapshot.child("status").getValue());
                    Boolean stat = false;
                    if(status == "true"){
                        stat = true;
                    }

                    WaterSource waterSource = new WaterSource(name,stat);

                    list.add(waterSource);

                }
                myAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout_menu){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "User signed out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
        }
        if(id == R.id.add_water_source){

        }
        if(id == R.id.home_menu){
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}