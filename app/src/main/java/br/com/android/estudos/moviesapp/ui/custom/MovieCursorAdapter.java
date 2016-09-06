package br.com.android.estudos.moviesapp.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.data.MoviesDataRequester;
import br.com.android.estudos.moviesapp.model.Movie;
import br.com.android.estudos.moviesapp.ui.MainActivity;

/**
 * Created by Dustin on 06/09/2016.
 */
public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.ViewHolder> {

    private Context mContext;

    public MovieCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final Movie movie = Movie.fromCursor(cursor);

        final String imageUrl = MoviesDataRequester.getPosterUrl( movie.getPosterPath() );
        ImageLoader.getInstance().displayImage(imageUrl, viewHolder.mImageView);

        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.ACTION_VIEW_MOVIE)
                        .putExtra(MainActivity.EXTRA_MOVIE, movie);
                mContext.sendBroadcast(intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_movie, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.image_poster);
        }
    }

}
