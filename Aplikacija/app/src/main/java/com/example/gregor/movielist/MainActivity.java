package com.example.gregor.movielist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements AddFrag.OnFragmentInteractionListener, HomeFrag.OnFragmentInteractionListener, SettingsFrag.OnFragmentInteractionListener{

        static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = findViewById(R.id.nv);
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Fragment fragment = null;
                        Class fragmentClass;
                        switch(menuItem.getItemId()) {
                            case R.id.nav_first_fragment:
                                fragmentClass = HomeFrag.class;
                                break;
                            case R.id.nav_second_fragment:
                                fragmentClass = AddFrag.class;
                                break;
                            case R.id.nav_settings:
                                fragmentClass = SettingsFrag.class;
                                break;
                            default:
                                fragmentClass = HomeFrag.class;
                        }

                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                        // Highlight the selected item has been done by NavigationView
                        menuItem.setChecked(true);
                        // Set action bar title
                        setTitle(menuItem.getTitle());
                        // Close the navigation drawer
                        mDrawer.closeDrawers();

                        return true;
                    }
                });

        FragmentManager fragmentManager = getSupportFragmentManager();

        try {
            fragmentManager.beginTransaction().replace(R.id.flContent, HomeFrag.class.newInstance()).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.Open,  R.string.Close);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
    public void clearOnClick(View v)
    {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settings.edit().clear().commit();
    }
    public void listonCLick(View v)
    {
        EditText idText= findViewById(R.id.listidEditText);
        String id=idText.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, "http://10.0.2.2/scrapeList.php?id="+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyApplication app = (MyApplication)getApplication();
                        @SuppressWarnings("unchecked")
                        List<Film> filmi=((List<Film>)new Gson().fromJson(response,  new TypeToken<List<Film>>(){}.getType()));
                        for (Film f :filmi
                             ) {
                            if(!app.contains(f))
                            app.add(f);
                        }
                        Toast.makeText(getApplicationContext(),"List added!", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Could not connect to the API!", Toast.LENGTH_LONG).show();
                Log.d("Networking","Connection unsuccessful! reason:"+ error.getMessage());
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    public void ToponClick(View v)
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, "http://10.0.2.2/scrapeTopMovies.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyApplication app = (MyApplication)getApplication();
                        @SuppressWarnings("unchecked")
                        List<Film> filmi=((List<Film>)new Gson().fromJson(response,  new TypeToken<List<Film>>(){}.getType()));
                        app.setFilmi(filmi);
                        Toast.makeText(getApplicationContext(),"Top movies added!", Toast.LENGTH_LONG).show();
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
    public void addIDOnClick(View v)
    {
        EditText idText= findViewById(R.id.idEditText);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://www.omdbapi.com/?apikey=1c2dbf9f&i="+idText.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Map info = new Gson().fromJson(response, Map.class);
                        Film film= new Film((String)info.get("Title"),Integer.parseInt((String)info.get("Year")),Double.parseDouble((String)info.get("imdbRating")),(String)info.get("imdbID"),(String)info.get("Poster"));
                        MyApplication app = (MyApplication)getApplication();
                        if(app.contains(film))
                            Toast.makeText(getApplicationContext(),"Movie "+(String)info.get("Title")+" already added!", Toast.LENGTH_LONG).show();
                        else {
                            app.add(film);
                            Toast.makeText(getApplicationContext(), "Movie " + (String) info.get("Title") + " added!", Toast.LENGTH_LONG).show();
                        }

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

    public void addTitleOnClick(View v)
    {
        EditText idText= findViewById(R.id.titleEditText);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://www.omdbapi.com/?apikey=1c2dbf9f&t="+idText.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Map info = new Gson().fromJson(response, Map.class);
                        Film film= new Film((String)info.get("Title"),Integer.parseInt((String)info.get("Year")),Double.parseDouble((String)info.get("imdbRating")),(String)info.get("imdbID"),(String)info.get("Poster"));
                        MyApplication app = (MyApplication)getApplication();
                        if(app.contains(film))
                            Toast.makeText(getApplicationContext(),"Movie "+(String)info.get("Title")+" already added!", Toast.LENGTH_LONG).show();
                        else {
                            app.add(film);
                            Toast.makeText(getApplicationContext(), "Movie " + (String) info.get("Title") + " added!", Toast.LENGTH_LONG).show();
                        }

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
