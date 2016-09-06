package br.com.android.estudos.moviesapp.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.data.MoviesDataRequester;
import br.com.android.estudos.moviesapp.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String ARG_MOVIE = "movie";
    private final SimpleDateFormat sdf;

    public MovieDetailsFragment() {
        sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = this.getArguments();
        final Movie movie = args.getParcelable(ARG_MOVIE);

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ((TextView)view.findViewById(R.id.text_title)).setText(movie.getOriginalTitle());
        ((TextView)view.findViewById(R.id.text_overview)).setText(movie.getOverview());

        String voteStr = movie.getVoteAvarage() + "/10";
        ((TextView)view.findViewById(R.id.text_vote)).setText(voteStr);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_poster);
        String url = MoviesDataRequester.getPosterUrl(movie.getPosterPath());
        ImageLoader.getInstance().displayImage(url, imageView);

        if (movie.getReleaseDate() != 0) {
            String year = sdf.format(movie.getReleaseDate());
            ((TextView)view.findViewById(R.id.text_year)).setText(year);
        }

        return view;
    }

    public static MovieDetailsFragment newInstance(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_MOVIE, movie);

        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        movieDetailsFragment.setArguments(bundle);

        return movieDetailsFragment;
    }
}
