package karan.com.popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Karan on 25-02-2016.
 */
public class MovieUtils implements Parcelable {

    protected String posterPath;
    protected String adult;
    protected String overView;
    protected String releaseDate;
    protected String title;
    protected String language;
    protected String popularity;
    protected String voteCount;
    protected String voteAverage;
    protected String id;

    public MovieUtils(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(adult);
        dest.writeString(overView);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeString(language);
        dest.writeString(popularity);
        dest.writeString(voteCount);
        dest.writeString(voteAverage);
        dest.writeString(id);
    }

    // Creator
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieUtils createFromParcel(Parcel in) {
            return new MovieUtils(in);
        }

        public MovieUtils[] newArray(int size) {
            return new MovieUtils[size];
        }
    };

    // "De-parcel object
    public MovieUtils(Parcel in) {
        posterPath = in.readString();
        adult = in.readString();
        overView = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        language = in.readString();
        popularity = in.readString();
        voteCount = in.readString();
        voteAverage = in.readString();
        id = in.readString();
    }
}
