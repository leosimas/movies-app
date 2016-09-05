package br.com.android.estudos.moviesapp.util;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
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
        values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 4.1);

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

    /*
    Students: The functions we provide inside of TestProvider use this utility class to test
            the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
            CTS tests.
    Note that this only tests that the onChange function is called; it does not test that the
    correct Uri is returned.
            */
    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
