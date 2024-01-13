package com.example.streamtv.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.streamtv.Activity.WatchlistDetails;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.R;

import java.util.List;

public class AdapterFavourites extends RecyclerView.Adapter<AdapterFavourites.ViewHolder>{

    private List<MovieModel> items;
    private Context mContext;
    private AdapterMovie.onSelectData onSelectData;
    public static String URLIMAGE = "https://image.tmdb.org/t/p/w780/";

    public interface onSelectData {
        void onSelected(MovieModel modelMovie);
    }
    public AdapterFavourites(List<MovieModel> items, Context mContext, AdapterMovie.onSelectData xSelectData) {
        this.items = items;
        this.mContext = mContext;
        this.onSelectData = xSelectData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav, parent, false);
        return new AdapterFavourites.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MovieModel data = items.get(position);
        String id = String.valueOf(data.getId());
        String title = String.valueOf(data.getTitle());
        String overView = String.valueOf(data.getOverview());
        String Popularity = String.valueOf(data.getPopularity());
        String Rating = String.valueOf(data.getVoteAverage());

        holder.tvTitle.setText(data.getTitle());
        holder.tvReleaseDate.setText(data.getReleaseDate());

        Glide.with(mContext)
                .load(URLIMAGE + data.getPosterPath())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_image_24)
                        .transform(new RoundedCorners(16)))
                .into(holder.imgPhoto);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSelectData.onSelected(data);
                Intent intent = new Intent(mContext, WatchlistDetails.class);
                intent.putExtra("id",id);
                intent.putExtra("title", title);
                intent.putExtra("overView", overView);
                intent.putExtra("popularity", Popularity);
                intent.putExtra("rating", Rating);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_fav);
            imgPhoto = itemView.findViewById(R.id.img_fav);
            tvTitle = itemView.findViewById(R.id.movie_name_fav);
            tvReleaseDate = itemView.findViewById(R.id.rel_date_fav);
        }
    }
}
