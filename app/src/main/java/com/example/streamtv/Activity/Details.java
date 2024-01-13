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
import com.example.streamtv.R;
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


public class Details extends AppCompatActivity {

    TextView tvMovieName, tvReleaseDate, tvPopularity, tvStoryLine;
    RecyclerView myrv;
    List<TrailerModel> TrailerList = new ArrayList<>();
    ImageButton share, watchListAdd;
    MovieModel modelMovie;
    RatingBar ratingBar;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    int Id;
    public static String URLFILM = "https://www.themoviedb.org/movie/";
    public static String URLIMAGE = "https://image.tmdb.org/t/p/w780/";
    double Rating;
    String movieURL, NameFilm, ReleaseDate, Popularity, StoryLine, Thumbnail;
    ImageView movie_poster;
    private boolean like = false;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().hide();

        ratingBar = findViewById(R.id.ratingBar2);
        movie_poster = findViewById(R.id.imageView);
        tvMovieName = findViewById(R.id.tvName);
        tvReleaseDate = findViewById(R.id.tvRelease);
        tvPopularity = findViewById(R.id.genre);
        tvStoryLine = findViewById(R.id.tvStoryLine);
        myrv = findViewById(R.id.recyclerview_trailer);
        share = findViewById(R.id.share);
        watchListAdd = findViewById(R.id.watchList);

        myrv = findViewById(R.id.recyclerview_trailer);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        modelMovie = (MovieModel) getIntent().getSerializableExtra("detailMovie");
        if (modelMovie != null) {

            Id = modelMovie.getId();
            NameFilm = modelMovie.getTitle();
            Rating = modelMovie.getVoteAverage();
            ReleaseDate = modelMovie.getReleaseDate();
            Popularity = modelMovie.getPopularity();
            StoryLine = modelMovie.getOverview();
            Thumbnail = modelMovie.getPosterPath();
            movieURL = URLFILM + "" + Id;

            tvMovieName.setText(NameFilm);
            tvReleaseDate.setText(ReleaseDate);
            tvPopularity.setText(Popularity);
            tvStoryLine.setText(StoryLine);
            tvMovieName.setSelected(true);

            float newValue = (float)Rating;
            ratingBar.setNumStars(5);
            ratingBar.setStepSize((float) 0.5);
            ratingBar.setRating(newValue / 2);

            Glide.with(this)
                    .load(URLIMAGE + Thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(movie_poster);

            myrv.setLayoutManager(new LinearLayoutManager(Details.this, LinearLayoutManager.HORIZONTAL, true));
            getTrailer(String.valueOf(Id));

        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String subject = modelMovie.getTitle();
                String description = modelMovie.getOverview();
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" + description + "\n\n" + movieURL);
                startActivity(Intent.createChooser(shareIntent, "Share with :"));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        final String id = mAuth.getCurrentUser().getUid();
        final String movieId = String.valueOf(modelMovie.getId());;
        mRootRef = FirebaseDatabase.getInstance().getReference().child("User").child(id).child("WatchList").child(movieId);
        watchListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like = !like;
                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (like) {
                            watchListAdd.setImageResource(R.drawable.baseline_playlist_added);
                            Map favorites = new HashMap();
                            favorites.put("img", modelMovie.getPosterPath());
                            favorites.put("title", modelMovie.getTitle());
                            favorites.put("releaseDate", modelMovie.getReleaseDate());
                            favorites.put("rating", modelMovie.getVoteAverage());
                            favorites.put("popularity", modelMovie.getPopularity());
                            favorites.put("storyline", modelMovie.getOverview());
                            mRootRef.setValue(favorites);
                            Toast.makeText(Details.this, "This movie is added to your list.", Toast.LENGTH_SHORT).show();
                        } else {
                            watchListAdd.setImageResource(R.drawable.baseline_playlist_add_24);
                            Toast.makeText(Details.this, "Removed movie from your list.", Toast.LENGTH_SHORT).show();
                            try {
                                mRootRef.setValue(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String e = databaseError.toString();
                        Toast.makeText(Details.this, e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getTrailer(String MovieId){
        progressDialog.show();
        String URL = "https://api.themoviedb.org/3/movie/"+ MovieId +"/videos?api_key=bb76a685d910dea80be3ebda6381944a";
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
                                TrailerList.add(dataApi);
                                showTrailerNow();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Details.this,"Failed to display data!", Toast.LENGTH_SHORT).show();
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

    private void showTrailerNow() {
        AdapterTrailer Adapter = new AdapterTrailer(TrailerList, Details.this);
        myrv.setAdapter(Adapter);
    }

}