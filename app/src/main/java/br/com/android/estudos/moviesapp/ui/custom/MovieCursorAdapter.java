package br.com.android.estudos.moviesapp.ui.custom;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.data.MoviesContract;
import br.com.android.estudos.moviesapp.data.MoviesDataRequester;
import br.com.android.estudos.moviesapp.model.Movie;

/**
 * Created by Dustin on 06/09/2016.
 */
public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.ViewHolder> {

    public MovieCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
//        Movie movie = Movie.fromCursor(cursor);

        final String posterPath = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
        final String imageUrl = MoviesDataRequester.getPosterUrl( posterPath );
        ImageLoader.getInstance().displayImage(imageUrl, viewHolder.mImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_movie, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.image_poster);
        }
    }

}
