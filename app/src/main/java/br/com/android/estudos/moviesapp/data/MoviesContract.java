package br.com.android.estudos.moviesapp.data;

import android.net.Uri;

/**
 * Created by Dustin on 02/09/2016.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "br.com.android.estudos.moviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static class MovieEntry {

        public static final String TABLE_NAME = "movie";

    }

}
