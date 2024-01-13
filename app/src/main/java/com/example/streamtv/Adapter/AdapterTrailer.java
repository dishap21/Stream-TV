package com.example.streamtv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamtv.Activity.Details;
import com.example.streamtv.Models.TrailerModel;
import com.example.streamtv.R;

import java.util.List;

public class AdapterTrailer extends RecyclerView.Adapter<AdapterTrailer.ViewHolder>{

    private List<TrailerModel> items;
    private Context mContext;

    public AdapterTrailer(List<TrailerModel> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer, parent, false);
        return new AdapterTrailer.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try{
            final TrailerModel data = items.get(position);
            holder.name.setText(data.getType());
            holder.trailerCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + data.getKey()));
                    mContext.startActivity(intent);
                }
            });
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            Toast.makeText(mContext,"Failed to display data!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CardView trailerCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trailer_name);
            trailerCard = itemView.findViewById(R.id.trailer_card);
        }
    }
}
