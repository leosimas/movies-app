package br.com.android.estudos.moviesapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.data.MoviesContract;
import br.com.android.estudos.moviesapp.data.PrefUtils;
import br.com.android.estudos.moviesapp.model.Movie;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    public static final String ACTION_VIEW_MOVIE = "view_movie";
    public static final String EXTRA_MOVIE = "movie";

    private static final String TAG_DETAIL = "tag_detail";

    private BroadcastReceiver mViewMovieRecever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if ( ACTION_VIEW_MOVIE.equals(action) ) {
                final Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);

                if ( mTwoPanes ) {
                    // TODO
                } else {
                    Intent startActivity = new Intent( context, DetailsActivity.class )
                            .putExtra( DetailsActivity.EXTRA_MOVIE, movie );
                    context.startActivity( startActivity );
                }
            }
        }
    };

    private boolean mTwoPanes;
    private MoviesContract.Sort mSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPanes = findViewById(R.id.fragment_container) != null;

        if ( mTwoPanes ) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, MovieDetailsFragment.newInstance( null ), TAG_DETAIL)
                .commit();
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheFileCount(30)
                .build();
        ImageLoader.getInstance().init(config);

        IntentFilter filter = new IntentFilter(ACTION_VIEW_MOVIE);
        this.registerReceiver(mViewMovieRecever, filter);

        MoviesContract.Sort newSort = PrefUtils.getSortMovies(this);
        this.onSortChanged(newSort);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(mViewMovieRecever);
    }

    @Override
    public void onSortChanged(MoviesContract.Sort newSort) {
        this.getSupportActionBar().setTitle( newSort.getTitleRes() );
        mSort = newSort;
    }
}
