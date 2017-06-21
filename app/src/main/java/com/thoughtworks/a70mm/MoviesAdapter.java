package com.thoughtworks.a70mm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shawast on 6/3/2017.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_row, parent, false);
        }
        Movie movie = getItem(position);
        ((TextView) convertView.findViewById(R.id.movie_name)).setText(movie.getMovieName());
        ((TextView) convertView.findViewById(R.id.movie_description)).setText(movie.getMovieDescription());
        ((TextView) convertView.findViewById(R.id.schedule_detail)).setText(movie.getMovieSchedule());
        return convertView;
    }
}
