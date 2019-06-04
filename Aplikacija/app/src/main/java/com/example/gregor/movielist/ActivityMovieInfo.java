package com.example.gregor.movielist;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityMovieInfo extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Bundle data = getIntent().getExtras();
        Film film = (Film) data.getParcelable("film");


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://www.omdbapi.com/?apikey=1c2dbf9f&i="+film.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ImageLoader loader=ImageLoader.getInstance();
                        loader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
                        TextView nameView=findViewById(R.id.MovieNameView);
                        TextView ratingView=findViewById(R.id.movieRatingView);
                        TextView yearView=findViewById(R.id.movieYearView);
                        TextView plotView=findViewById(R.id.moviePlotView);
                        TextView actorView=findViewById(R.id.movieActorsView);
                        TextView directorView=findViewById(R.id.movieDirectorView);
                        ImageView imageView=findViewById(R.id.movieImageView);


                        Map info = new Gson().fromJson(response, Map.class);
                        nameView.setText((String)info.get("Title"));
                        yearView.setText((String)info.get("Year"));
                        plotView.setText((String)info.get("Plot"));
                        actorView.setText((String)info.get("Actors"));
                        ratingView.setText((String)info.get("imdbRating"));
                        directorView.setText((String)info.get("Director"));
                        loader.displayImage((String)info.get("Poster"), imageView,new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY).build());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Could not connect to the API!", Toast.LENGTH_LONG).show();
                Log.d("Networking","Connection unsucsessful! reason:"+ error.getMessage());
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        queue.add(request);
    }
}
