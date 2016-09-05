package br.com.android.estudos.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;
import br.com.android.estudos.moviesapp.util.TestUtils;

/**
 * Created by Dustin on 05/09/2016.
 */
public class TestContentProvider extends AndroidTestCase {

    public void testGetType() {
        ContentResolver contentResolver = mContext.getContentResolver();
        String type = contentResolver.getType(MovieEntry.CONTENT_URI);
        assertEquals("Error : MovieEntry.CONTENT_URI getType should be MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        long id = 1;
        type = contentResolver.getType(MovieEntry.buildMovieUri(id));
        assertEquals("Error : MovieEntry.CONTENT_URI getType should be MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.deleteAll();
    }

    private void deleteAll() {
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.delete(MovieEntry.CONTENT_URI, null, null);

        Cursor cursor = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: failed to delete all movies", cursor.getCount(), 0);
        cursor.close();
    }

    public void testInsert() {
        final long movieId = this.insertSharkMovie();

        ContentResolver contentResolver = mContext.getContentResolver();

        Cursor c = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue(c.getCount() == 1);
        assertTrue(c.moveToFirst());

        final long serverId = c.getLong( c.getColumnIndex(MovieEntry.COLUMN_SERVER_ID) );

        c.close();

        final String newPath = "/newpath.jpeg";

        // insert with same server id
        ContentValues values = TestUtils.getNewMovieValues_Shark();
        values.put(MovieEntry.COLUMN_POSTER_PATH, newPath);
        values.put(MovieEntry.COLUMN_SERVER_ID, serverId);

        Uri newUri = contentResolver.insert(
                MovieEntry.CONTENT_URI,
                values
        );
        // this should fail every time:
        // assertEquals(MovieEntry.buildMovieUri(movieId), newUri);
        // the REPLACE constraint deletes the old and insert the new, creating a new id

        // query to compare updated value:
        c = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue(c.getCount() == 1);
        assertTrue(c.moveToFirst());

        String currentPosterPath = c.getString( c.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH) );
        assertEquals(newPath, currentPosterPath);

    }

    public void testUpdate() {
        long movieId = this.insertSharkMovie();

        ContentResolver contentResolver = mContext.getContentResolver();

        final double voteAverage = 1.1;

        // update vote average
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

        int updatedRows = contentResolver.update(
                MovieEntry.CONTENT_URI,
                values,
                MovieEntry._ID + " = ?",
                new String[]{Long.toString(movieId)}
        );
        assertTrue("Error while updating", updatedRows == 1);

        Cursor c = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue(c.moveToFirst());

        final double currentVote = c.getDouble( c.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE) );
        assertTrue("Error : update failed, column " + MovieEntry.COLUMN_VOTE_AVERAGE +
                    ", should be " + voteAverage + " instead of " + currentVote,
                currentVote == voteAverage );

        c.close();

        // update with newUri
        final String newPath = "newpathhere";
        values.put(MovieEntry.COLUMN_POSTER_PATH, newPath);

        Uri insertedUri = MovieEntry.buildMovieUri( movieId );
        updatedRows = contentResolver.update(
                insertedUri,
                values,
                null,
                null
        );
        assertTrue("Error while updating", updatedRows == 1);

        c = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue(c.moveToFirst());

        final String currentPath = c.getString( c.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH) );
        assertEquals("Error : update failed, column " + MovieEntry.COLUMN_POSTER_PATH,
                newPath,
                currentPath);

        c.close();

        // update with newUri and selection
        updatedRows = contentResolver.update(
                insertedUri,
                values,
                MovieEntry.COLUMN_ORIGINAL_TITLE + " = ?",
                new String[]{ "Sharknado" }
        );
        assertTrue("Error while updating", updatedRows == 0);

    }

    private long insertSharkMovie() {
        ContentValues values = TestUtils.getNewMovieValues_Shark();

        TestUtils.TestContentObserver tco = TestUtils.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        Uri uri = contentResolver.insert(
                MovieEntry.CONTENT_URI,
                values
        );
        assertTrue("Error : insert failed ", uri != null);

        tco.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(tco);

        // query with new uri:
        Cursor c = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: failed to query new inserted movie", c.moveToFirst());

        TestUtils.validateCurrentRecord("Error : validating MoviEntry.", c, values);
        c.close();

        // query for all:
        final long movieId = ContentUris.parseId(uri);
        assertTrue("Error : insert failed " + uri.toString(), movieId != 1);

        c = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: failed to query new inserted movie", c.moveToFirst());

        TestUtils.validateCurrentRecord("Error : validating MoviEntry.", c, values);
        c.close();

        return movieId;
    }

    public void testDeleteById() {
        final long movieId = this.insertSharkMovie();

        Uri uri = MovieEntry.buildMovieUri(movieId);

        ContentResolver contentResolver = mContext.getContentResolver();
        int deletedRows = contentResolver.delete(
                uri,
                null,
                null
        );
        assertTrue(deletedRows == 1);
    }

    private ContentValues[] createNewMovies() {

        ContentValues arrayValues[] = new ContentValues[5];

        for (int i = 0; i < arrayValues.length; i++) {
            ContentValues values = new ContentValues();
            final int count = (i+1);

            values.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Sharknado " + count);
            values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "very good! " + count);
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/123455_"+count+".jpeg");
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, 1473033600000l + count); // 2016/09/05
            values.put(MoviesContract.MovieEntry.COLUMN_SERVER_ID, count + 10);
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, count * 10 / 5f);

            arrayValues[i] = values;
        }

        return arrayValues;
    }

    public void testBulkInsert() {

        ContentValues[] newMovies = this.createNewMovies();

        ContentResolver contentResolver = mContext.getContentResolver();

        int insertedRows = contentResolver.bulkInsert(MovieEntry.CONTENT_URI, newMovies);
        assertEquals("Error bulkinsert returned insertedRows", newMovies.length, insertedRows);

        Cursor c = contentResolver.query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error query after bulkinsert", newMovies.length, c.getCount());
        assertTrue(c.moveToFirst());

        for (int i = 0; i < newMovies.length; i++) {
            TestUtils.validateCurrentRecord("Error validating records", c, newMovies[i]);

            c.moveToNext();
        }

        c.close();

    }

}
