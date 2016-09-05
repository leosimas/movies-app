package br.com.android.estudos.moviesapp.data;

import android.content.ContentResolver;
import android.test.AndroidTestCase;

/**
 * Created by Dustin on 05/09/2016.
 */
public class TestContentProvider extends AndroidTestCase {

    public void testGetType() {
        ContentResolver contentResolver = mContext.getContentResolver();
        String type = contentResolver.getType(MoviesContract.MovieEntry.CONTENT_URI);
        assertEquals("Error : MovieEntry.CONTENT_URI getType should be MovieEntry.CONTENT_TYPE",
                MoviesContract.MovieEntry.CONTENT_TYPE, type);

        long id = 1;
        type = contentResolver.getType(MoviesContract.MovieEntry.buildMovieUri(id));
        assertEquals("Error : MovieEntry.CONTENT_URI getType should be MovieEntry.CONTENT_ITEM_TYPE",
                MoviesContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

//    public void testInsert() {
//    }

}
