package com.example.gregor.movielist;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyApplication extends Application {
    private List<Film> filmi = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
    }


    public boolean containsAll(@NonNull Collection<?> c) {
        return filmi.containsAll(c);
    }

    public boolean contains(Film o) {
        for (Film a:filmi)
        {
            if(a.getId().equals(o.getId()))
                return true;
        }
        return  false;
    }
public void sort()
{
    Collections.sort(filmi, new Comparator<Film>() {
    @Override
    public int compare(Film u1, Film u2) {
        return Double.compare(u2.getRating(),u1.getRating());
    }
});
}
    public List<Film> getFilmi() {
        return filmi;
    }

    public void setFilmi(List<Film> filmi) {
        this.filmi = filmi;
    }

    public int size() {
        return filmi.size();
    }

    public boolean add(Film film) {
        return filmi.add(film);
    }

    public boolean remove(Object o) {
        return filmi.remove(o);
    }

    public boolean addAll(@NonNull Collection<? extends Film> c) {
        return filmi.addAll(c);
    }

    public Film get(int index) {
        return filmi.get(index);
    }

    public int indexOf(Object o) {
        return filmi.indexOf(o);
    }
}
