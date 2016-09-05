package br.com.android.estudos.moviesapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import br.com.android.estudos.moviesapp.data.MoviesContract;

/**
 * Created by Dustin on 05/09/2016.
 */
public class TestUtils extends AndroidTestCase {

    public static ContentValues getNewMovieValues_Shark() {
        ContentValues values = new ContentValues();

        values.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Shark");
        values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "very good!");
        values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/123455.jpeg");
        values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, 1473033600000l); // 2016/09/05
        values.put(MoviesContract.MovieEntry.COLUMN_SERVER_ID, 123);
        values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVARAGE, 4.1);

        return values;
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);

            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
