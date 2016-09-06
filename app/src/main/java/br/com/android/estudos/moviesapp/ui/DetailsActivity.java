package br.com.android.estudos.moviesapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.com.android.estudos.moviesapp.R;
import br.com.android.estudos.moviesapp.model.Movie;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";

    private static final String TAG_FRAG = "tag_frag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Movie movie = this.getIntent().getParcelableExtra( EXTRA_MOVIE );

            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, MovieDetailsFragment.newInstance(movie), TAG_FRAG)
                .commit();
        }

    }

}
