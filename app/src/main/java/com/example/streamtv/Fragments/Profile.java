package com.example.streamtv.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.streamtv.Activity.AboutUs;
import com.example.streamtv.Activity.Login;
import com.example.streamtv.Activity.Watchlist;
import com.example.streamtv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class Profile extends Fragment {


    CardView aboutUs, watchlist;
    Button sign_out;
    FirebaseAuth auth;
    TextView userName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        userName = RootView.findViewById(R.id.user_name);
        showUserName();
        sign_out = RootView.findViewById(R.id.signOut);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                auth.signOut();
                sendToLogin();
            }
        });

        aboutUs = (CardView) RootView.findViewById(R.id.AboutUs);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAboutUs = new Intent(Profile.this.getActivity(), AboutUs.class);
                startActivity(goToAboutUs);
            }
        });

        watchlist = (CardView) RootView.findViewById(R.id.watchList);
        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTowatch = new Intent(Profile.this.getActivity(), Watchlist.class);
                startActivity(goTowatch);
            }
        });
        return RootView;
    }

    private void showUserName() {
        final String id = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.child(id).child("userName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String hiUser = task.getResult().getValue(String.class);
                userName.setText(hiUser);
            }
        });
    }

    @SuppressLint({"RestrictedApi", "UseRequireInsteadOfGet"})
    private void sendToLogin() {
        Intent sendToLogin = new Intent(getActivity(), Login.class);
        startActivity(sendToLogin);
        Objects.requireNonNull(getActivity()).finish();
    }
}