package br.com.android.estudos.moviesapp.data;

import android.content.ContentValues;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.android.estudos.moviesapp.BuildConfig;
import br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;
import br.com.android.estudos.moviesapp.model.Movie;
import br.com.android.estudos.moviesapp.model.MovieReview;
import br.com.android.estudos.moviesapp.model.MovieVideo;

/**
 * Created by Dustin on 02/09/2016.
 */
public class MoviesDataRequester {

    private static final String LOG_TAG = MoviesDataRequester.class.getSimpleName();

    private static final SimpleDateFormat SDF_RELEASE_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private static final String BASE_URL_API = "http://api.themoviedb.org/3/";
    private static final String BASE_URL_POSTERS = "http://image.tmdb.org/t/p";

    private static @Nullable String doGet(Uri.Builder builder) {
        HttpURLConnection urlConnection;
        BufferedReader reader;

        try {
            builder.appendQueryParameter("api_key", BuildConfig.MOVIEDB_APPKEY);

            URL url = new URL(builder.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if ( inputStream == null ) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader( inputStream ));
            StringBuilder buffer = new StringBuilder();

            String line;
            while ( (line = reader.readLine()) != null ) {
                buffer.append(line);
            }

            if ( buffer.length() == 0 ) {
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "error", e);
        }

        return null;
    }

    public static @Nullable String getVideos(Movie movie) {
        Uri.Builder builder = Uri.parse( BASE_URL_API ).buildUpon()
                .appendPath("movie")
                .appendPath( Long.toString( movie.getServerId() ) )
                .appendPath("videos");

        return doGet( builder );
    }

    public static @Nullable String getReviews(Movie movie) {
        Uri.Builder builder = Uri.parse( BASE_URL_API ).buildUpon()
                .appendPath("movie")
                .appendPath( Long.toString( movie.getServerId() ) )
                .appendPath("reviews");

        return doGet( builder );
    }

    public static @Nullable List<MovieReview> parseReviews(String jsonString) {
//        {
//            "id": 49026,
//                "page": 1,
//                "results": [
//            {
//                "id": "5010553819c2952d1b000451",
//                    "author": "Travis Bell",
//                    "content": "I felt like this was a tremendous end to Nolan's Batman trilogy. The Dark Knight Rises may very well have been the weakest of all 3 films but when you're talking about a scale of this magnitude, it still makes this one of the best movies I've seen in the past few years.\r\n\r\nI expected a little more _Batman_ than we got (especially with a runtime of 2:45) but while the story around the fall of Bruce Wayne and Gotham City was good I didn't find it amazing. This might be in fact, one of my only criticisms—it was a long movie but still, maybe too short for the story I felt was really being told. I feel confident in saying this big of a story could have been split into two movies.\r\n\r\nThe acting, editing, pacing, soundtrack and overall theme were the same 'as-close-to-perfect' as ever with any of Christopher Nolan's other films. Man does this guy know how to make a movie!\r\n\r\nYou don't have to be a Batman fan to enjoy these movies and I hope any of you who feel this way re-consider. These 3 movies are without a doubt in my mind, the finest display of comic mythology ever told on the big screen. They are damn near perfect.",
//                    "url": "http://j.mp/QSjAK2"
//            }
//            ],
//            "total_pages": 1,
//                "total_results": 3
//        }

        if (jsonString == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            //
            JSONArray results = jsonObject.getJSONArray("results");

            final int length = results.length();
            List<MovieReview> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                JSONObject reviewJson = results.getJSONObject(i);
                MovieReview mv = new MovieReview();

                mv.setAuthor( reviewJson.getString( "author" ) );
                mv.setContent( reviewJson.getString( "content" ) );

                list.add( mv );
            }

            return list;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "error", e);
        }

        return null;

    }

    public static @Nullable List<MovieVideo> parseVideos(String jsonString) {
//        {
//            "id": 550,
//                "results": [
//            {
//                "id": "533ec654c3a36854480003eb",
//                    "iso_639_1": "en",
//                    "key": "SUXWAEX2jlg",
//                    "name": "Trailer 1",
//                    "site": "YouTube",
//                    "size": 720,
//                    "type": "Trailer"
//            }
//            ]
//        }

        if (jsonString == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            //
            JSONArray results = jsonObject.getJSONArray("results");

            final int length = results.length();
            List<MovieVideo> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                JSONObject videoJson = results.getJSONObject(i);
                MovieVideo mv = new MovieVideo();

                mv.setKey( videoJson.getString( "key" ) );
                mv.setName( videoJson.getString( "name" ) );
                mv.setSite( videoJson.getString( "site" ) );
                mv.setType( videoJson.getString( "type" ) );
                mv.setSize( videoJson.getInt( "size" ) );

                list.add( mv );
            }

            return list;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "error", e);
        }

        return null;

    }

    public static @Nullable String getMovies(MoviesContract.Sort sort) {
        // TODO GET movies
        // http://api.themoviedb.org/3/movie/popular
        // http://api.themoviedb.org/3/movie/top_rated

        Uri.Builder builder = Uri.parse( BASE_URL_API ).buildUpon()
                .appendPath("movie");
        if ( sort == MoviesContract.Sort.TOP_RATED ) {
            builder.appendPath("top_rated");
        } else { // popular
            builder.appendPath("popular");
        }

        return doGet( builder );
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

    public static String getPosterUrl( String posterPath ) {
        // url example: http://image.tmdb.org/t/p/w185/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg
        // posterPath = "/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg"
        if ( posterPath == null ) {
            return null;
        }

        if (!posterPath.startsWith("/")) {
            posterPath = "/"+posterPath;
        }

        return Uri.parse( BASE_URL_POSTERS ).buildUpon()
                .appendPath( "w185" ) // default image size for mobile
                .build().toString() +
                posterPath;
    }




}
