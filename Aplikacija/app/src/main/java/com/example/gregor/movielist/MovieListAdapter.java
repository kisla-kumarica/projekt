package com.example.gregor.movielist;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gregor.movielist.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private List<Film> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MovieListAdapter(Context context, List<Film> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        loader.init(ImageLoaderConfiguration.createDefault(context));

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_list_row, parent, false);
        return new ViewHolder(view);
    }
    private ImageLoader loader=ImageLoader.getInstance();
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Film film = mData.get(position);
        holder.nameText.setText(film.getNaslov());
        holder.yearText.setText(Integer.toString(film.getLeto()));
        holder.ratingText.setText(String.valueOf(film.getRating()));

        //Picasso.get().load(film.getImglink().replace("https","http")).fit().error(R.drawable.ic_launcher_foreground).placeholder(R.drawable.ic_launcher_background).into(holder.movieimg);
        loader.displayImage(film.getImglink().substring(0,film.getImglink().indexOf("_V1_")+4)+".jpg", holder.movieimg,new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY).build());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, ratingText, yearText;
        ImageView movieimg;
        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.movieName);
            ratingText = itemView.findViewById(R.id.movieRating);
            yearText = itemView.findViewById(R.id.movieYear);
            movieimg = itemView.findViewById(R.id.movieImage);
           // itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Film getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
