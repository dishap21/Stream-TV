package com.example.streamtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.R;

import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder>{

    private List<MovieModel> items;
    private Context mContext;
    private AdapterMovie.onSelectData onSelectData;
    private double Rating;
    public static String URLIMAGE = "https://image.tmdb.org/t/p/w780/";

    public interface onSelectData {
        void onSelected(MovieModel modelMovie);
    }
    public AdapterSearch(List<MovieModel> items, Context mContext, AdapterMovie.onSelectData xSelectData) {
        this.items = items;
        this.mContext = mContext;
        this.onSelectData = xSelectData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sv, parent, false);
        return new AdapterSearch.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MovieModel data = items.get(position);

        Rating = data.getVoteAverage();
        holder.tvTitle.setText(data.getTitle());
        holder.tvReleaseDate.setText(data.getReleaseDate());

        float newValue = (float)Rating;
        holder.ratingBar.setNumStars(5);
        holder.ratingBar.setStepSize((float) 0.5);
        holder.ratingBar.setRating(newValue / 2);

        Glide.with(mContext)
                .load(URLIMAGE + data.getPosterPath())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_image_24)
                        .transform(new RoundedCorners(16)))
                .into(holder.imgPhoto);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectData.onSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        public ImageView imgPhoto;
        public TextView tvTitle;
        public TextView tvReleaseDate;
        public RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_sv);
            imgPhoto = itemView.findViewById(R.id.img);
            tvTitle = itemView.findViewById(R.id.movie_name);
            tvReleaseDate = itemView.findViewById(R.id.rel_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
