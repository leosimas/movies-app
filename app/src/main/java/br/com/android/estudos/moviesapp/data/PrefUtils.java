package br.com.android.estudos.moviesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.android.estudos.moviesapp.R;

/**
 * Created by Dustin on 02/09/2016.
 */
public class PrefUtils {

    public static String getSortMovies(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString( context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_default) );
    }

}
