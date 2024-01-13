package com.example.streamtv.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.streamtv.Adapter.AdapterTrailer;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.Models.TrailerModel;
import com.example.streamtv.Models.User;
import com.example.streamtv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WatchlistDetails extends AppCompatActivity {

    ImageView moviePic;
    TextView movieTitle, movieStoryLine, moviePopularity, movieRelease;
    RatingBar ratingBar;
    FirebaseAuth auth;
    ImageButton watchlist, share;
    private boolean like = true;
    RecyclerView myrv;
    List<TrailerModel> TrailerList2 = new ArrayList<>();
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist_details);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        moviePic = findViewById(R.id.imageViewWD);
        ratingBar = findViewById(R.id.ratingBar2WD);
        watchlist = findViewById(R.id.watchListWD);
        final String user_id = auth.getCurrentUser().getUid();
        Intent intent = getIntent();
        String movieId = Objects.requireNonNull(intent.getExtras()).getString("id");
        String img = Objects.requireNonNull(intent.getExtras()).getString("img");
        String title = Objects.requireNonNull(intent.getExtras()).getString("title");
        String releaseDate = Objects.requireNonNull(intent.getExtras()).getString("releaseDate");
        String rating = Objects.requireNonNull(intent.getExtras()).getString("rating");
        String popularity = Objects.requireNonNull(intent.getExtras()).getString("popularity");
        String storyline = Objects.requireNonNull(intent.getExtras()).getString("storyline");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user_id).child("WatchList").child(movieId);
        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like = !like;
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (like) {
                            watchlist.setImageResource(R.drawable.baseline_playlist_added);
                            Map favorites = new HashMap();
                            favorites.put("img", img);
                            favorites.put("title", title);
                            favorites.put("releaseDate", releaseDate);
                            favorites.put("rating", rating);
                            favorites.put("popularity", popularity);
                            favorites.put("storyline", storyline);
                            databaseReference.setValue(favorites);
                            Toast.makeText(WatchlistDetails.this, "This movie is added to your list.", Toast.LENGTH_SHORT).show();
                        } else {
                            watchlist.setImageResource(R.drawable.baseline_playlist_add_24);
                            Toast.makeText(WatchlistDetails.this, "Removed movie from your list.", Toast.LENGTH_SHORT).show();
                            try {
                                databaseReference.setValue(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String e = databaseError.toString();
                        Toast.makeText(WatchlistDetails.this, e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        share = findViewById(R.id.shareWD);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String movieURL = "https://www.themoviedb.org/movie/";
                Intent intent = getIntent();
                String subject = Objects.requireNonNull(intent.getExtras()).getString("title");
                String description = Objects.requireNonNull(intent.getExtras()).getString("overView");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" + description + "\n\n" + movieURL);
                startActivity(Intent.createChooser(shareIntent, "Share with :"));
            }
        });
        movieTitle = findViewById(R.id.tvNameWD);
        movieStoryLine = findViewById(R.id.tvStoryLineWD);
        moviePopularity = findViewById(R.id.popularityWD);
        movieRelease = findViewById(R.id.tvReleaseWD);
        myrv = findViewById(R.id.recyclerview_trailerWD);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        myrv.setLayoutManager(new LinearLayoutManager(WatchlistDetails.this, LinearLayoutManager.HORIZONTAL, true));
        showDetails();
    }

    private void showDetails() {
        final String id = auth.getCurrentUser().getUid();
        Intent intent = getIntent();
        String movieId = Objects.requireNonNull(intent.getExtras()).getString("id");
        showTrailer2(movieId);
        //Toast.makeText(WatchlistDetails.this, "The movie id is "+ movieId, Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(id).child("WatchList").child(movieId);
        databaseReference.child("popularity").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String popularity = task.getResult().getValue(String.class);
                moviePopularity.setText(popularity);
            }
        });
        databaseReference.child("releaseDate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String releaseDate = task.getResult().getValue(String.class);
                movieRelease.setText(releaseDate);
            }
        });
        databaseReference.child("storyline").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String popularity = task.getResult().getValue(String.class);
                movieStoryLine.setText(popularity);
            }
        });
        databaseReference.child("title").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String popularity = task.getResult().getValue(String.class);
                movieTitle.setText(popularity);
            }
        });
        databaseReference.child("img").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String URLIMAGE = "https://image.tmdb.org/t/p/w780/";
                String img = task.getResult().getValue(String.class);
                Glide.with(WatchlistDetails.this)
                        .load(URLIMAGE + img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(moviePic);
            }
        });
        databaseReference.child("rating").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int rating = task.getResult().getValue(int.class);
                float newValue = (float)rating;
                ratingBar.setNumStars(5);
                ratingBar.setStepSize((float) 0.5);
                ratingBar.setRating(newValue / 2);
            }
        });
    }

    private void showTrailer2(String movieId) {
        progressDialog.show();
        String URL = "https://api.themoviedb.org/3/movie/"+ movieId +"/videos?api_key=bb76a685d910dea80be3ebda6381944a";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TrailerModel dataApi = new TrailerModel();
                                dataApi.setKey(jsonObject.getString("key"));
                                dataApi.setType(jsonObject.getString("type"));
                                TrailerList2.add(dataApi);
                                showTrailerNow2();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WatchlistDetails.this,"Failed to display data!", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void showTrailerNow2() {
        AdapterTrailer Adapter = new AdapterTrailer(TrailerList2, WatchlistDetails.this);
        myrv.setAdapter(Adapter);
    }
}