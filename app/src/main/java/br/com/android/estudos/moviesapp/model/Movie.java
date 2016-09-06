package br.com.android.estudos.moviesapp.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import br.com.android.estudos.moviesapp.data.MoviesContract;
import br.com.android.estudos.moviesapp.data.MoviesContract.MovieEntry;

/**
 * Created by Dustin on 06/09/2016.
 */
public class Movie implements Parcelable {

    private long id;
    private long serverId;
    private String posterPath;
    private String originalTitle;
    private String overview;
    private long releaseDate;
    private double voteAvarage;

    public Movie() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAvarage() {
        return voteAvarage;
    }

    public void setVoteAvarage(double voteAvarage) {
        this.voteAvarage = voteAvarage;
    }

    // parcelable:
    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

    public Movie(Parcel parcel) {
        this.id = parcel.readLong();
        this.serverId = parcel.readLong();
        this.releaseDate = parcel.readLong();
        this.voteAvarage = parcel.readDouble();
        this.posterPath = parcel.readString();
        this.originalTitle = parcel.readString();
        this.overview = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(serverId);
        dest.writeLong(releaseDate);
        dest.writeDouble(voteAvarage);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Movie fromCursor(Cursor c) {
        Movie m = new Movie();

        m.id = c.getLong( c.getColumnIndex(MovieEntry._ID) );
        m.serverId = c.getLong( c.getColumnIndex(MovieEntry.COLUMN_SERVER_ID) );
        m.releaseDate = c.getLong( c.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE) );
        m.voteAvarage = c.getDouble( c.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE) );
        m.posterPath = c.getString( c.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH) );
        m.originalTitle = c.getString( c.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE) );
        m.overview = c.getString( c.getColumnIndex(MovieEntry.COLUMN_OVERVIEW) );

        return m;
    }
}
