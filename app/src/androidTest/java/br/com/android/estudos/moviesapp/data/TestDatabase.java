package br.com.android.estudos.moviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import java.util.HashSet;

import br.com.android.estudos.moviesapp.util.TestUtils;

/**
 * Created by Dustin on 05/09/2016.
 */
public class TestDatabase extends AndroidTestCase {

    public void testCreate() {
        SQLiteDatabase db = initDbForTesting(true);

        // TABLES test:
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: tables not created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove( c.getString(0) );
        } while( c.moveToNext() );
        assertTrue("Error: missing tables : " + tableNameHashSet, tableNameHashSet.isEmpty());

        c.close();


        /// MOVIES TABLE:
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MoviesContract.MovieEntry._ID);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_SERVER_ID);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: missing MOVIES columns : " + movieColumnHashSet, movieColumnHashSet.isEmpty());
        c.close();

        db.close();

    }

    @NonNull
    private SQLiteDatabase initDbForTesting(boolean writable) {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);

        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db;
        if ( writable )
            db = helper.getWritableDatabase();
        else
            db = helper.getReadableDatabase();
        assertEquals("Error: database not opened", true, db.isOpen());
        return db;
    }

    public void testInsert() {
        SQLiteDatabase db = initDbForTesting( true );

        ContentValues values = TestUtils.getNewMovieValues_Shark();
        long rowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
        assertTrue("Error: failed to insert", rowId != -1);

        Cursor c = db.query(MoviesContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: querying new movie:", c.moveToFirst());

        TestUtils.validateCurrentRecord("failed retriving the data", c, values);

        c.close();

        db.close();
    }

}
