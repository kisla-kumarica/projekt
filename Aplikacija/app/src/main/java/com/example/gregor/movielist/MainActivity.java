package com.example.gregor.movielist;

import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MovieListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://10.0.2.2/scrapeTopMovies.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        RecyclerView movieList= findViewById(R.id.MainMovieList);
                        Gson gson= new Gson();
                        List<Film> filmi= new ArrayList<>();
                        filmi=gson.fromJson(response, new TypeToken<List<Film>>(){}.getType());
                        movieList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter = new MovieListAdapter(getApplicationContext(), filmi);
                        movieList.setAdapter(adapter);

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
