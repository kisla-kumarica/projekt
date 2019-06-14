package com.example.gregor.movielist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeechService;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.googlecode.tesseract.android.TessBaseAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends BaseActivity implements AddFrag.OnFragmentInteractionListener, HomeFrag.OnFragmentInteractionListener, SettingsFrag.OnFragmentInteractionListener{


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);



        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);


        OpenCVLoader.initDebug();


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

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        menuItem.setChecked(true);
                        setTitle(menuItem.getTitle());
                        mDrawer.closeDrawers();

                        return true;
                    }
                });

    }
    @Override
    protected void onStart()
    {
        super.onStart();

        //nvDrawer.getMenu().getItem(0).
        HomeFrag frag = new HomeFrag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, HomeFrag.newInstance()).commit();
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
        settings.edit().clear().apply();
        MyApplication app = (MyApplication)getApplication();
        app.setFilmi(new ArrayList<Film>());
        Toast.makeText(getApplicationContext(), "Data cleared!", Toast.LENGTH_LONG).show();
    }
    File image;
    public void addImageOnClick(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            image = null;
            try {
                image = File.createTempFile( imageFileName, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES) );
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save a file: path for use with ACTION_VIEW intents
            String path = image.getAbsolutePath();
            photoFile=image;
            if(photoFile!=null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            startActivityForResult(takePictureIntent, 1024);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1024 && resultCode == RESULT_OK) {
            Bitmap imageBitmap=BitmapFactory.decodeFile(image.getAbsolutePath());
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            Mat src= new Mat(imageBitmap.getWidth(),imageBitmap.getHeight(),CvType.CV_8U);
            Utils.bitmapToMat(imageBitmap,src);
            Imgproc.cvtColor(src,src,Imgproc.COLOR_RGB2GRAY);

            Imgproc.threshold(src,src,0,255,Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
            Imgproc.blur(src,src,new Size(5,5));
            Utils.matToBitmap(src,imageBitmap);
            src.release();
            TessBaseAPI tess = new TessBaseAPI();

            tess.init(Environment.getExternalStorageDirectory().toString(), "eng");
            tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
            tess.setImage(imageBitmap);
            String recognizedText = tess.getUTF8Text();
            Pattern pat = Pattern.compile("\\d{9}");
            Matcher match=pat.matcher(recognizedText);
            if(match.find()) {
                String text="ls"+recognizedText.substring(match.start(),match.start()+9);

                Toast.makeText(this, "Najden list ID:" + text, Toast.LENGTH_LONG).show();
                addList(text);
            }
           else
            {
                pat = Pattern.compile("\\d{7}");
                match=pat.matcher(recognizedText);
                if(match.find()) {
                    String text="tt"+recognizedText.substring(match.start(),match.start()+7);
                    Log.d("OCR", "Najden film ID:" + text);
                    addMovie(text);
                }
                else
                    Toast.makeText(this, "Nic najdeno", Toast.LENGTH_LONG).show();
            }
            tess.end();
        }
    }
    public void addList(String id)
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, "http://10.0.2.2/scrapeList.php?id="+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("404")) {
                            MyApplication app = (MyApplication) getApplication();
                            @SuppressWarnings("unchecked")
                            List<Film> filmi = new Gson().fromJson(response, new TypeToken<List<Film>>() {
                            }.getType());
                            for (Film f : filmi) {
                                if (!app.contains(f))
                                    app.add(f);
                            }
                            Toast.makeText(getApplicationContext(), "List added!", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "List not found!", Toast.LENGTH_LONG).show();
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
    public void listonCLick(View v)
    {
        final EditText idText= findViewById(R.id.listidEditText);
        String id=idText.getText().toString();
addList(id);
        idText.setText("");
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
                        List<Film> filmi= new Gson().fromJson(response,  new TypeToken<List<Film>>(){}.getType());
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
    public void addMovie(String id)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://www.omdbapi.com/?apikey=1c2dbf9f&i="+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Map info = new Gson().fromJson(response, Map.class);
                        if(info.size()!=2)
                        {
                            Film film= new Film((String)info.get("Title"),Integer.parseInt((String)info.get("Year")),Double.parseDouble((String)info.get("imdbRating")),(String)info.get("imdbID"),(String)info.get("Poster"));
                            MyApplication app = (MyApplication)getApplication();
                            if(app.contains(film))
                                Toast.makeText(getApplicationContext(),"Movie "+ info.get("Title") +" already added!", Toast.LENGTH_LONG).show();
                            else {
                                app.add(film);
                                Toast.makeText(getApplicationContext(), "Movie " + info.get("Title") + " added!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Movie not found!!", Toast.LENGTH_LONG).show();

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
        final EditText idText= findViewById(R.id.idEditText);
        addMovie(idText.getText().toString());
        idText.setText("");
    }

    public void addTitleOnClick(View v)
    {
        final EditText idText= findViewById(R.id.titleEditText);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://www.omdbapi.com/?apikey=1c2dbf9f&t="+idText.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Map info = new Gson().fromJson(response, Map.class);
                        if(info.size()!=2) {
                            Film film = new Film((String) info.get("Title"), Integer.parseInt((String) info.get("Year")), Double.parseDouble((String) info.get("imdbRating")), (String) info.get("imdbID"), (String) info.get("Poster"));
                            MyApplication app = (MyApplication) getApplication();
                            if (app.contains(film))
                                Toast.makeText(getApplicationContext(), "Movie " + info.get("Title") + " already added!", Toast.LENGTH_LONG).show();
                            else {
                                app.add(film);
                                Toast.makeText(getApplicationContext(), "Movie " + info.get("Title") + " added!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Movie not found!!", Toast.LENGTH_LONG).show();

                        idText.setText("");


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
