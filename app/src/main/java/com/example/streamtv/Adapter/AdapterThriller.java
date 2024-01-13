package com.example.streamtv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.streamtv.Models.MovieModel;
import com.example.streamtv.R;

import java.util.List;

public class AdapterThriller extends RecyclerView.Adapter<AdapterThriller.ViewHolder>{

    private List<MovieModel> items;
    private AdapterAction.onSelectData onSelectData;
    private Context mContext;
    public static String URLIMAGE = "https://image.tmdb.org/t/p/w780/";

    public interface onSelectData {
        void onSelected(MovieModel modelMovie);
    }


    public AdapterThriller(Context context, List<MovieModel> items, AdapterAction.onSelectData xSelectData) {
        this.mContext = context;
        this.items = items;
        this.onSelectData = xSelectData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new AdapterThriller.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MovieModel data = items.get(position);

        Glide.with(mContext)
                .load(URLIMAGE + data.getPosterPath())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_image_24)
                        .transform(new RoundedCorners(16)))
                .into(holder.imgPhoto);

        holder.cv3.setOnClickListener(new View.OnClickListener() {
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


    class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cv3;
        public ImageView imgPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            cv3 = itemView.findViewById(R.id.container);
            imgPhoto = itemView.findViewById(R.id.imgPoster);
        }
    }
}
