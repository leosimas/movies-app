package br.com.android.estudos.moviesapp.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.data.MoviesContract;
import br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;
import br.com.android.estudos.moviesapp.data.MoviesDataRequester;
import br.com.android.estudos.moviesapp.data.PrefUtils;
import br.com.android.estudos.moviesapp.ui.custom.MovieCursorAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 100;
    private static final int GRID_COLUMNS = 3;

    private RecyclerView mRecyclerView;
    private MovieCursorAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = getActivity();

        mGridLayoutManager = new GridLayoutManager(context, GRID_COLUMNS);
        mRecyclerView.setLayoutManager( mGridLayoutManager );

        Cursor cursor = context.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if ( cursor.getCount() == 0 ) {
            this.updateMovies();
        }

        mAdapter = new MovieCursorAdapter( context, cursor );
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateMovies() {
        Context context = this.getActivity();
        final String sort = PrefUtils.getSortMovies(context);
        new MoviesTask(context).execute(sort);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity(),
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class MoviesTask extends AsyncTask<String, Void, Void> {

        private Context mContext;

        public MoviesTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(String... params) {
            if ( params == null || params.length == 0 ) {
                return null;
            }

            final String sort = params[0];

            String moviesJson = MoviesDataRequester.getMovies(mContext, sort);
            if ( moviesJson == null ) {
                return null;
            }

            ContentValues[] contentValues = MoviesDataRequester.parseMovies(moviesJson);

            mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, contentValues);

            return null;
        }
    }

}
