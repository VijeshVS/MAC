package com.vshetty.mac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    DatabaseReference databaseReference;
    DatabaseReference waterReference;
    Toolbar toolbar;
    TextView welcomeMsg;
    ActionBarDrawerToggle toggle;


    RecyclerView recyclerView;
    ProgressBar progressBar;
    MineAdapter myAdapter;
    ArrayList<WaterSource> list;

    String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getUIComponents();
        recyclerViewSetter();
        navbarSetter();
        getUserName();



        // Event listener which handles the Recycler view -> basically shows up the selected water_sources
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot water_list = snapshot.child(firstName).child("select_ws");

                list.clear();
                for(DataSnapshot dataSnapshot: water_list.getChildren()){
                    // get the name of the water_source for each iteration
                    String name_water = String.valueOf(dataSnapshot.getKey());

                    // to store the result of the database retrieval
                    final String[] status = {""};

                    // Thread to keep the status of the my_water_sources reliable
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            waterReference.child(name_water).child("status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    status[0] = task.getResult().getValue().toString();
                                    for(WaterSource waterSource:list){
                                        if(waterSource.name.equals(name_water)){
                                            if(status[0].equals("true")){
                                                waterSource.status = true;
                                                status[0] = "false";
                                                break;
                                            }
                                        }
                                    }

                                    myAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    t.start();

                    WaterSource waterSource = new WaterSource(name_water,false);
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


    // Navigator Item Selector
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
            startActivity(new Intent(this, watersourceList.class));
            finish();
        }
        if(id == R.id.home_menu){
            
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // sets the firstname for display in UI and also for further processing
    public void getUserName() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String mail = firebaseUser.getEmail();
        char[] charArray = mail.toCharArray();
        int i = 0;
        firstName = "";
        while(charArray[i]!='@'){
            firstName += charArray[i];
            i++;
        }

        welcomeMsg.setText("Welcome "+firstName+"!!");

    }

    public void getUIComponents(){
        welcomeMsg = findViewById(R.id.welcomeMSG);
        drawerLayout = findViewById(R.id.drawerLayout_home);
        navigationView = findViewById(R.id.navigationView_home);
        toolbar = findViewById(R.id.toolbar_home);
        progressBar = findViewById(R.id.progressBarID);
        recyclerView = findViewById(R.id.mywatersourceList);

        databaseReference = FirebaseDatabase.getInstance("https://realtimewqms-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        waterReference = FirebaseDatabase.getInstance("https://realtimewqms-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water_sources");
    }

    public void recyclerViewSetter(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MineAdapter(this,list);
        recyclerView.setAdapter(myAdapter);
    }

    public void navbarSetter(){
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigration_open,R.string.navigration_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

}