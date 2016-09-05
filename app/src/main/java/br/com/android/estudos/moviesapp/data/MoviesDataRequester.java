package br.com.android.estudos.moviesapp.data;

import android.content.Context;
import android.net.Uri;
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

import br.com.android.estudos.moviesapp.BuildConfig;
import br.com.android.estudos.moviesapp.R;

/**
 * Created by Dustin on 02/09/2016.
 */
public class MoviesDataRequester {

    private static final String LOG_TAG = MoviesDataRequester.class.getSimpleName();

    public static void getMovies(Context context) {
        // TODO GET movies
        // http://api.themoviedb.org/3/movie/popular
        // http://api.themoviedb.org/3/movie/top_rated

        String sortMovies = PrefUtils.getSortMovies(context);


        HttpURLConnection urlConnection;
        BufferedReader reader;

        String jsonString = null;
        try {
            Uri.Builder builder = Uri.parse( "http://api.themoviedb.org/3/" ).buildUpon()
                    .appendPath("movie");
            if ( context.getString(R.string.pref_sort_top_rated).equals( sortMovies ) ) {
                builder.appendPath("top_rated");
            } else { // TOP_RATED
                builder.appendPath("popular");
            }
            builder.appendQueryParameter("app_key", BuildConfig.MOVIEDB_APPKEY);

            URL url = new URL(builder.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if ( inputStream == null ) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader( inputStream ));
            StringBuilder buffer = new StringBuilder();

            String line;
            while ( (line = reader.readLine()) != null ) {
                buffer.append(line);
            }

            if ( buffer.length() == 0 ) {
                return;
            }


            jsonString = buffer.toString();

//        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "error", e);
        }

        try {
            saveData(jsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "error", e);
        }


    }

    private static void saveData(String jsonString) throws JSONException {
        if ( jsonString == null ) {
            return;
        }

        JSONObject jsonObject = new JSONObject( jsonString );

        //
        JSONArray results = jsonObject.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {

        }
    }


    // ?api_key = ...

    // TODO GET Movie poster URL:



}
