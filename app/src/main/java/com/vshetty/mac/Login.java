package com.vshetty.mac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {


    TextView registerView;

    public static FirebaseAuth auth;
    private EditText loginEmail,loginPassword;
    private Button loginBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        registerView = findViewById(R.id.registerLink);

        registerView.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.edtTxt);
        loginPassword = findViewById(R.id.edtPass);
        loginBtn = findViewById(R.id.logButt);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if(email.equals("")){
                    loginEmail.setError("Please enter your email");
                }else if(pass.equals("")){
                    loginPassword.setError("Please enter your password");
                }else if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,HomePage.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()!=null){
            Toast.makeText(this, "User already logged in !!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,HomePage.class));
            finish();
        }else{
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int r = v.getId();

        if(r == R.id.registerLink){
            startActivity(new Intent(Login.this,Register.class));
            finish();
        }
    }
}