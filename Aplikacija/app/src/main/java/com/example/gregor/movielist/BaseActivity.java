package com.example.gregor.movielist;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import static android.app.PendingIntent.getActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onPause() {
        super.onPause();
        MyApplication app = (MyApplication)getApplication();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putString("filmi",new Gson().toJson(app.getFilmi())).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication app = (MyApplication)getApplication();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pref=prefs.getString("filmi", "");
        if(pref!="") {
            @SuppressWarnings("unchecked")
            List<Film> filmi= new Gson().fromJson(pref,  new TypeToken<List<Film>>(){}.getType());
            app.setFilmi(filmi);
            app.sort();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }



}
