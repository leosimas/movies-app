package br.com.android.estudos.moviesapp.data;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dustin on 05/09/2016.
 */
public class TestDataRequester extends AndroidTestCase {

    public void testRequest() {
        String moviesJson = MoviesDataRequester.getMovies(mContext);
        assertFalse("json returned : " + moviesJson, moviesJson == null);

        Pattern p = Pattern.compile("\"id\"");
        Matcher m = p.matcher(moviesJson);
        int idsCounter = 0;
        while ( m.find() ){
            idsCounter++;
        }

        ContentValues[] contentValues = MoviesDataRequester.parseMovies(moviesJson);
        assertEquals(idsCounter, contentValues.length);

    }

}
