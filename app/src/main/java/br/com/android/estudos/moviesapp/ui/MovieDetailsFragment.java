package br.com.android.estudos.moviesapp.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String ARG_MOVIE = "movie";

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);


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
