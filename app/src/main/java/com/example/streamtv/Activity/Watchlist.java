package com.example.streamtv.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.streamtv.Adapter.AdapterFavourites;
import com.example.streamtv.Adapter.AdapterMovie;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Watchlist extends AppCompatActivity implements AdapterMovie.onSelectData{

    private List<MovieModel> favList = new ArrayList<>();
    private RecyclerView myrv;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    LinearLayout notSaved;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist2);
        getSupportActionBar().hide();

        notSaved = findViewById(R.id.nosaved);
        myrv = findViewById(R.id.recyclerview_fav);
        myrv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        getFavourites();
    }

    private void getFavourites() {
        progressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        final String id = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference().child("User").child(id).child("WatchList");
        mRootRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favList = new ArrayList<>();
                HashMap favorites = (HashMap) dataSnapshot.getValue();
                if (favorites != null) {
                    for (Object movie : favorites.keySet()) {
                        String title = (String) dataSnapshot.child(movie.toString()).child("title").getValue();
                        String img = (String) dataSnapshot.child(movie.toString()).child("img").getValue();
                        String release_date = (String) dataSnapshot.child(movie.toString()).child("releaseDate").getValue();
                        favList.add(new MovieModel(Integer.parseInt((String) movie), title, release_date,img));
                    }
                }
                else{
                    Toast.makeText(Watchlist.this, "Noting on list", Toast.LENGTH_SHORT).show();
                }

                if (favList.isEmpty()) {
                    myrv.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    notSaved.setVisibility(View.VISIBLE);
                } else {
                    showFavMovie();
                    progressDialog.dismiss();
                    notSaved.setVisibility(View.GONE);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(delete);
                    itemTouchHelper.attachToRecyclerView(myrv);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String s = databaseError.toString();
                Toast.makeText(Watchlist.this, "Error:" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFavMovie() {
        AdapterFavourites adapter_fav = new AdapterFavourites(favList, getApplicationContext(), this);
        myrv.setAdapter(adapter_fav);
        adapter_fav.notifyDataSetChanged();
    }

    @Override
    public void onSelected(MovieModel modelMovie) {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra("detailMovie", modelMovie);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback delete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder deleteMovie = new AlertDialog.Builder(Watchlist.this);
            deleteMovie.setTitle("Delete from watchlist?");
            deleteMovie.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int position = viewHolder.getAbsoluteAdapterPosition();
                    String movieId = String.valueOf(favList.get(position).getId());
                    favList.remove(position);
                    Objects.requireNonNull(myrv.getAdapter()).notifyItemRemoved(position);
                    deleteFromFirebase(movieId);
                }
            });
            deleteMovie.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Objects.requireNonNull(myrv.getAdapter()).notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                }
            });
            deleteMovie.show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red))
                    .addSwipeRightLabel("DELETE").addActionIcon(R.drawable.baseline_delete_24)
                    .create().decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteFromFirebase(String movieId) {
        final String id = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(id).child("WatchList");
        reference.child(movieId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "The movie has been deleted.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Not able to delete the movie.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}