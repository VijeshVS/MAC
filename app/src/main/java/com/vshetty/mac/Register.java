package com.vshetty.mac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {
    TextView loginView;
    private FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference reference;
    private EditText signupEmail,signupPassword,signupTemp;
    private Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginView = findViewById(R.id.loginLink);
        loginView.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.emailAdd);
        signupPassword = findViewById(R.id.confPass);
        signupButton = findViewById(R.id.registerBtn);
        signupTemp = findViewById(R.id.firstPass);

        StringBuilder f = new StringBuilder();


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String firstPass = signupTemp.getText().toString().trim();


                if(!(pass.equals(firstPass))) {
                    signupPassword.setError("Password do not match ! Please try again");
                }
                else if(user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                else if(pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                }
                else{
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this,HomePage.class));
                                finish();

                            }else{
                                Toast.makeText(Register.this, "Register Failed !"+task.getException().getMessage() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });



    }


    @Override
    public void onClick(View v) {
        int r = v.getId();

        if(r == R.id.loginLink){
            startActivity(new Intent(Register.this,Login.class));
            finish();
        }
    }
}