package com.thoughtworks.a70mm;

import java.util.Date;
import java.util.List;

/**
 * Created by shawast on 6/3/2017.
 */
public class Movie {
    public String movieName;
    private String movieDescription;
    private String movieSchedule;

    public Movie(String movieName, String movieDescription, String movieSchedule) {
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.movieSchedule = movieSchedule;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public String getMovieSchedule() {
        return movieSchedule;
    }
}
