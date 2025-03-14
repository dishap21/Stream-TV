package com.example.streamtv.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamtv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    private TextView sign_up;
    private Button login;
    private EditText email,pass;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        sign_up= findViewById(R.id.registerBtn);
        login= findViewById(R.id.loginBtn);
        email= findViewById(R.id.loginEmailEt);
        pass= findViewById(R.id.PasswordEt);

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Logging In User");
        progressDialog.setMessage("Getting your account ready..");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String user_email = email.getText().toString();
                String user_password = pass.getText().toString();
                try{
                    if(user_email.isEmpty() || user_password.isEmpty()){
                        throw new Exception("All fields must be filled");
                    }
                    if (!(Patterns.EMAIL_ADDRESS.matcher(user_email).matches())){
                        throw new Exception("Invalid Email");
                    }
                    loginUser(user_email,user_password);

                }
                catch (Exception e){
                    try {
                        throw new Exception("All fields must be filled");
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(Login.this, Register.class);
                startActivity(startIntent);
            }
        });
    }

    private void loginUser(String user_email, String user_password) {
        mAuth.signInWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent mainIntent= new Intent(Login.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish(); // The user can't come back to this page
                }
                else{
                    progressDialog.dismiss();
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(Login.this, "Invalid Password", Toast.LENGTH_LONG).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidUserException){
                        Toast.makeText(Login.this, "Email not exist", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}