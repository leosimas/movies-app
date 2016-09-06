package br.com.android.estudos.moviesapp.ui;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.PopupMenuCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private static final int LOADER_ID = 100;
    private static final int GRID_COLUMNS = 3;

    private RecyclerView mRecyclerView;
    private MovieCursorAdapter mAdapter;

    public MoviesFragment() {
        this.setHasOptionsMenu(true);
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

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, GRID_COLUMNS);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

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
        new MoviesTask(this.getActivity()).execute();
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
        Log.d(LOG_TAG, "onLoadFinished, data.isClosed() = " + data.isClosed());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.action_sort:
//                this.showSortPopupMenu(item.getActionView());
                View view = getActivity().findViewById(item.getItemId());
                Log.d(LOG_TAG, "onOptionsItemSelected view = " + view);
                this.showSortPopupMenu( view );
                return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortPopupMenu(View actionView) {
        PopupMenu popupMenu = new PopupMenu(this.getActivity(), actionView);
        popupMenu.inflate(R.menu.menu_sort);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MoviesContract.Sort newSort;

                final int id = item.getItemId();
                switch (id) {
                    case R.id.action_sort_popular:
                        newSort = MoviesContract.Sort.POPULAR;
                        break;
                    case R.id.action_sort_top_rated:
                        newSort = MoviesContract.Sort.TOP_RATED;
                        break;
                    default:
                        return false;
                }

                if ( PrefUtils.getSortMovies(getContext()) == newSort ) {
                    return true;
                }

                ((Callback)getActivity()).onSortChanged(newSort);

                PrefUtils.setSortMovies(getActivity(), newSort);
                updateMovies();

                getLoaderManager().restartLoader(LOADER_ID, null, MoviesFragment.this);
                return true;
            }
        });
        popupMenu.show();
    }

    private class MoviesTask extends AsyncTask<String, Void, Void> {

        private final Context mContext;
        private final MoviesContract.Sort mSort;

        public MoviesTask(Context context) {
            this.mContext = context;
            mSort = PrefUtils.getSortMovies(context);
        }

        @Override
        protected Void doInBackground(String... params) {
            String moviesJson = MoviesDataRequester.getMovies(mSort);
            if ( moviesJson == null ) {
                return null;
            }

            ContentValues[] contentValues = MoviesDataRequester.parseMovies(moviesJson);
            if ( contentValues == null ) {
                return null;
            }

            ContentResolver contentResolver = mContext.getContentResolver();

            int deleted = contentResolver.delete(MovieEntry.CONTENT_URI, null, null);
            Log.d(LOG_TAG, "deleted rows = " + deleted);

            int inserted = contentResolver.bulkInsert(MovieEntry.CONTENT_URI, contentValues);
            Log.d(LOG_TAG, "inserted rows = " + inserted);

            return null;
        }
    }

    public interface Callback {

        void onSortChanged(MoviesContract.Sort newSort);

    }

}
