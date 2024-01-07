package com.vshetty.mac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

public class mainPage extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Button up;

    Handler handler;
    public LineChart lineChart,lineChart2;
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    ArrayList<ILineDataSet> iLineDataSets1 = new ArrayList<>();
    LineData lineData;
    private List<String> xValues;
    private ListView listView;
    public ArrayList<Entry> dataVals = new ArrayList<Entry>();
    public ArrayList<Entry> turbdata = new ArrayList<Entry>();

    Button dataBtn ;
    DatabaseReference databaseReference;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ActionBarDrawerToggle toggle;

    TextView turbval;
    TextView tempval;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        setupToolbar();

        turbval = findViewById(R.id.turbval);
        tempval = findViewById(R.id.tempval);

        lineChart = findViewById(R.id.lineChart);
        lineChart2 = findViewById(R.id.lineChart2);

        databaseReference = FirebaseDatabase.getInstance("https://realtimewqms-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water_sources");

        databaseReference.child("ganga").child("sensors").child("temp").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                long values = task.getResult().getChildrenCount();
                int n = 1;
                for(DataSnapshot snapshot: task.getResult().getChildren() ) {
                    if(values-6 < n) {
                        float x = Float.parseFloat(String.valueOf(snapshot.getKey()));
                        float y = Float.parseFloat(String.valueOf(snapshot.getValue()));
                        dataVals.add(new Entry(x, y));
                    }
                    n++;
                }

                showChart(dataVals);
            }
        });

        databaseReference.child("ganga").child("sensors").child("turb").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                long values = task.getResult().getChildrenCount();
                int n = 1;

                for(DataSnapshot snapshot: task.getResult().getChildren() ) {
                    if(values-6 < n) {
                        float x = Float.parseFloat(String.valueOf(snapshot.getKey()));
                        float y = Float.parseFloat(String.valueOf(snapshot.getValue()));
                        turbdata.add(new Entry(x, y));
                    }
                    n++;
                }

                showChart2(turbdata);
            }
        });


        // Updating graph and values of temp and turb in real-time
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    // chart update
                    databaseReference.child("ganga").child("sensors").child("temp").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            long n = task.getResult().getChildrenCount();

                            for(DataSnapshot snapshot: task.getResult().getChildren() ) {
                                if(n==1) {
                                    float x = Float.parseFloat(String.valueOf(snapshot.getKey()));
                                    float y = Float.parseFloat(String.valueOf(snapshot.getValue()));

                                    int s = dataVals.size();
                                    Entry temp = dataVals.get(s-1);

                                    Entry e = new Entry(x,y);
                                    if(temp.getX() != x && temp.getY() != y) {
                                        dataVals.remove(0);
                                        dataVals.add(e);
                                    }
                                }
                                n--;

                            }
                            showChart(dataVals);
                        }
                    });

                    databaseReference.child("ganga").child("sensors").child("temp").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            long n = task.getResult().getChildrenCount();
                            for(DataSnapshot snapshot: task.getResult().getChildren() ) {
                                if(n==1) {
                                    final float y = Float.parseFloat(String.valueOf(snapshot.getValue()));
                                   runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String f = String.valueOf(y);
                                            tempval.setText(f + "°C");
                                        }
                                    });
                                }
                                n--;

                            }
                        }
                    });


                    databaseReference.child("ganga").child("sensors").child("turb").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            long n = task.getResult().getChildrenCount();

                            for(DataSnapshot snapshot: task.getResult().getChildren() ) {
                                if(n==1) {
                                    float x = Float.parseFloat(String.valueOf(snapshot.getKey()));
                                    float y = Float.parseFloat(String.valueOf(snapshot.getValue()));

                                    int s = turbdata.size();
                                    Entry temp = turbdata.get(s-1);

                                    Entry e = new Entry(x,y);
                                    if(temp.getX()!=x && temp.getY()!=y) {
                                        turbdata.remove(0);
                                        turbdata.add(e);
                                    }
                                }
                                n--;
                            }
                            showChart2(turbdata);
                        }
                    });

                    databaseReference.child("ganga").child("sensors").child("turb").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            long n = task.getResult().getChildrenCount();
                            for(DataSnapshot snapshot: task.getResult().getChildren() ) {
                                if(n==1) {
                                    final float y = Float.parseFloat(String.valueOf(snapshot.getValue()));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String f = String.valueOf(y);
                                            turbval.setText(f + " NTU");
                                        }
                                    });
                                }
                                n--;

                            }
                        }
                    });

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        Toast.makeText(mainPage.this, "Runtime Exception!! didn't sleep", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        t.start();
//        up.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mainPage.this, "Called", Toast.LENGTH_SHORT).show();
//
//                dataVals.add(new Entry(j,i ));
//                if(dataVals.size() > 6 )
//                    dataVals.remove(0);
//                j++;
//                i+=10;
//
//                showChart(dataVals);
//            }
//        });

    }

    public void showChart(ArrayList<Entry> dataVals) {
        LineDataSet lineDataSet = new LineDataSet(dataVals,"Temperature");
        lineDataSet.setColor(Color.RED);
        LineData lineData1 = new LineData(lineDataSet);
        Description description = new Description();
        description.setText(" ");
        lineChart.setDescription(description);
        lineChart.clear();
        lineChart.setData(lineData1);
        lineChart.invalidate();
        lineChart.animateX(3000,Easing.EaseInSine);
    }

    public void showChart2(ArrayList<Entry> turbdata){
        LineDataSet lineDataSet1 = new LineDataSet(turbdata,"Turbidity");
        LineData lineData2  = new LineData(lineDataSet1);
        Description description = new Description();
        description.setText(" ");
        lineChart2.setDescription(description);
        lineChart2.clear();
        lineChart2.setData(lineData2);
        lineChart2.invalidate();
        lineChart2.animateX(3000,Easing.EaseInSine);
    }


    @Override
    public void onClick(View v) {
        int k = v.getId();


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
            startActivity(new Intent(this, watersourceList.class));
            finish();
        }
        if(id == R.id.home_menu){
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setupToolbar(){
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigration_open,R.string.navigration_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


}