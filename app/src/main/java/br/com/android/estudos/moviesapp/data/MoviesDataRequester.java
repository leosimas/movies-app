package br.com.android.estudos.moviesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.android.estudos.moviesapp.BuildConfig;
import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;

/**
 * Created by Dustin on 02/09/2016.
 */
public class MoviesDataRequester {

    private static final String LOG_TAG = MoviesDataRequester.class.getSimpleName();

    private static final SimpleDateFormat SDF_RELEASE_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);;

    public static @Nullable String getMovies(Context context) {
        // TODO GET movies
        // http://api.themoviedb.org/3/movie/popular
        // http://api.themoviedb.org/3/movie/top_rated

        String sortMovies = PrefUtils.getSortMovies(context);


        HttpURLConnection urlConnection;
        BufferedReader reader;

        try {
            Uri.Builder builder = Uri.parse( "http://api.themoviedb.org/3/" ).buildUpon()
                    .appendPath("movie");
            if ( context.getString(R.string.pref_sort_top_rated).equals( sortMovies ) ) {
                builder.appendPath("top_rated");
            } else { // TOP_RATED
                builder.appendPath("popular");
            }
            builder.appendQueryParameter("api_key", BuildConfig.MOVIEDB_APPKEY);

            URL url = new URL(builder.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if ( inputStream == null ) {
                return sortMovies;
            }

            reader = new BufferedReader(new InputStreamReader( inputStream ));
            StringBuilder buffer = new StringBuilder();

            String line;
            while ( (line = reader.readLine()) != null ) {
                buffer.append(line);
            }

            if ( buffer.length() == 0 ) {
                return sortMovies;
            }


            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "error", e);
        }

        return null;
    }

    public static @Nullable ContentValues[] parseMovies(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            //
            JSONArray results = jsonObject.getJSONArray("results");

            final int length = results.length();
            ContentValues contentValues[] = new ContentValues[length];
            for (int i = 0; i < length; i++) {
                JSONObject movieJson = results.getJSONObject(i);
                ContentValues cv = new ContentValues();

                cv.put(MovieEntry.COLUMN_SERVER_ID, movieJson.getLong( "id" ));
                cv.put(MovieEntry.COLUMN_POSTER_PATH, movieJson.getString("poster_path"));
                Long dateRelease = parseReleaseDate( movieJson.getString("release_date") );
                cv.put(MovieEntry.COLUMN_RELEASE_DATE, dateRelease);
                cv.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movieJson.getString("original_title"));
                cv.put(MovieEntry.COLUMN_OVERVIEW, movieJson.getString("overview"));
                cv.put(MovieEntry.COLUMN_VOTE_AVERAGE, movieJson.getDouble("vote_average"));

                contentValues[i] = cv;
            }

            return contentValues;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "error", e);
        }

        return null;
    }

    private static Long parseReleaseDate(String releaseDate) {
        try {
            return SDF_RELEASE_DATE.parse(releaseDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO GET Movie poster URL:



}
