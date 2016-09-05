package br.com.android.estudos.moviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;

/**
 * Created by Dustin on 02/09/2016.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static final int MOVIES = 100;
    private static final int MOVIE = 101;

    private MoviesDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String auth = MoviesContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(auth, MoviesContract.PATH_MOVIE, MOVIES);
        uriMatcher.addURI(auth, MoviesContract.PATH_MOVIE + "/#", MOVIE);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(this.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = URI_MATCHER.match(uri);
        Cursor cursor;

        switch (match) {
            case MOVIES:
            case MOVIE:
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( id > 0) {
                    returnUri = MovieEntry.buildMovieUri(id);
                } else {
                    throw new SQLException("Failed to insert row into : " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        int deletedRows;

        switch (match) {
            case MOVIES:
                if (selection == null ) { // is it really necessary?
                    selection = "1"; // to delete all
                }
                deletedRows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE:
                long id = ContentUris.parseId(uri);
                deletedRows = db.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = ?", new String[]{ Long.toString( id ) });
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        if (deletedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        int updatedRows;

        switch (match) {
            case MOVIES:
                updatedRows = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE:
                long id = ContentUris.parseId(uri);
                final String selectId = MovieEntry._ID + " = ?";
                final String selectArgId = Long.toString(id);

                if ( selection == null ) {
                    selection = selectId;
                    selectionArgs = new String[]{ selectArgId };
                } else {
                    selection = selectId + " AND " + selection;
                    if ( selectionArgs != null ) {
                        String[] aux = new String[selectionArgs.length + 1];
                        aux[0] = selectArgId;
                        for (int i = 0; i < selectionArgs.length; i++) {
                            aux[i+1] = selectionArgs[i];
                        }
                        selectionArgs = aux;
                    } else {
                        selectionArgs = new String[]{ selectArgId };
                    }
                }

                updatedRows = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (updatedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MOVIES:
                db.beginTransaction();

                int insertedRows = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert( MovieEntry.TABLE_NAME, null, value );
                        if ( id > 0 ) {
                            insertedRows++;
                        }
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange( uri, null );

                return insertedRows;

            default:
                return super.bulkInsert(uri, values);
        }


    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
