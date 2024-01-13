package com.example.streamtv.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamtv.Models.User;
import com.example.streamtv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private Button register;
    private TextView login;
    private EditText mail, pass, userName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        register = findViewById(R.id.Register_Btn);
        login = findViewById(R.id.Login_Btn);
        mail = findViewById(R.id.REmailet);
        userName = findViewById(R.id.RNameEt);
        pass = findViewById(R.id.RPasswordEt);

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setTitle("Creating User");
        progressDialog.setMessage("Your account is being created.");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String user_email = mail.getText().toString();
                String user_password = pass.getText().toString();
                String user_name = userName.getText().toString();
                try{
                    if(user_email.isEmpty() || user_password.isEmpty()){
                        throw new Exception("All fields must be filled");
                    }
                    if(user_password.length()<6){
                        throw new Exception("Password length should be more than 6 characters");
                    }
                    registerUser(user_name, user_email,user_password);

                }
                catch (Exception e){
                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(Register.this, Login.class);
                startActivity(startIntent);
            }
        });
    }

    private void registerUser(String user_name, String user_email, String user_password) {
        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();
                if(task.isSuccessful()){
                    User user = new User(userName.getText().toString().trim(), mail.getText().toString().trim(),
                            pass.getText().toString().trim());
                    String id = task.getResult().getUser().getUid();
                    database.getReference().child("User").child(id).setValue(user);
                    Toast.makeText(Register.this, "User created.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}