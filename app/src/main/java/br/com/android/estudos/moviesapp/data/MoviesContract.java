package br.com.android.estudos.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import br.com.android.estudos.moviesapp.R;

/**
 * Created by Dustin on 02/09/2016.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "br.com.android.estudos.moviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE ;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE ;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public enum Sort {
        POPULAR( R.string.pref_sort_popular, R.string.title_popular ),
        TOP_RATED(R.string.pref_sort_top_rated, R.string.title_top_rated);

        private int prefValue;
        private int titleRes;

        Sort(int prefValue, int titleRes) {
            this.prefValue = prefValue;
            this.titleRes = titleRes;
        }

        public static Sort getByValue(Context context, String value) {
            if ( context.getString(TOP_RATED.prefValue).equals( value ) ) {
                return TOP_RATED;
            }

            return POPULAR;
        }

        public int getPrefValue() {
            return prefValue;
        }

        public int getTitleRes() {
            return titleRes;
        }
    }

}
