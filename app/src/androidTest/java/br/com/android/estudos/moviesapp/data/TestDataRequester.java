package br.com.android.estudos.moviesapp.data;

import android.content.ContentValues;
import android.test.AndroidTestCase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.android.estudos.moviesapp.R;

import static br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;

/**
 * Created by Dustin on 05/09/2016.
 */
public class TestDataRequester extends AndroidTestCase {

    private static final String LOG_TAG = TestDataRequester.class.getSimpleName();



    public void testRequestAndParser() {
        // request
        String sortMovies = mContext.getString(R.string.pref_sort_popular);

        String moviesJson = MoviesDataRequester.getMovies(mContext, sortMovies);
        assertFalse("json returned : " + moviesJson, moviesJson == null);

        // parser
        ContentValues[] contentValues = MoviesDataRequester.parseMovies(moviesJson);


        // comparing:
        try {
            JSONArray jsonArray = new JSONObject(moviesJson).getJSONArray("results");

            int elementsCounter = jsonArray.length();
            assertEquals(elementsCounter, contentValues.length);

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            for (int i = 0; i < elementsCounter; i++) {
                ContentValues cv = contentValues[i];
                JSONObject jo = jsonArray.getJSONObject(i);

                assertEquals( jo.getInt("id") , (int) cv.getAsInteger(MovieEntry.COLUMN_SERVER_ID) );
                assertEquals( jo.getDouble("vote_average") , cv.getAsDouble(MovieEntry.COLUMN_VOTE_AVERAGE) );
                assertEquals( jo.getString("overview"), cv.getAsString(MovieEntry.COLUMN_OVERVIEW) ) ;
                assertEquals( jo.getString("original_title"), cv.getAsString(MovieEntry.COLUMN_ORIGINAL_TITLE) ) ;
                assertEquals( jo.getString("poster_path"), cv.getAsString(MovieEntry.COLUMN_POSTER_PATH) );
                String formattedDate = sdf.format( cv.getAsLong(MovieEntry.COLUMN_RELEASE_DATE) );
                assertEquals( jo.getString("release_date"), formattedDate  );
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "parsing failed", e);
            fail("parsing data failed, see logcat");
        }

    }

    public void testRequestTopRated() {
        // request popular
        String sortMovies = mContext.getString(R.string.pref_sort_popular);

        String moviesJson = MoviesDataRequester.getMovies(mContext, sortMovies);
        assertFalse("json returned : " + moviesJson, moviesJson == null);

        ContentValues[] contentValuesPopular = MoviesDataRequester.parseMovies(moviesJson);


        // top rated:
        sortMovies = mContext.getString(R.string.pref_sort_top_rated);
        moviesJson = MoviesDataRequester.getMovies(mContext, sortMovies);
        assertFalse("json returned : " + moviesJson, moviesJson == null);

        ContentValues[] contentValuesTop = MoviesDataRequester.parseMovies(moviesJson);
        Integer idTopRated = contentValuesTop[0].getAsInteger(MovieEntry.COLUMN_SERVER_ID);
        Integer idPopular = contentValuesPopular[0].getAsInteger(MovieEntry.COLUMN_SERVER_ID);
        assertTrue( idTopRated != idPopular );

    }

    public void testPosterUrl() {
        final String posterPath = "/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg";
        final String expectedUrl = "http://image.tmdb.org/t/p/w185" + posterPath;

        final String url = MoviesDataRequester.getPosterUrl( posterPath );
        assertEquals(expectedUrl, url);
    }

}
