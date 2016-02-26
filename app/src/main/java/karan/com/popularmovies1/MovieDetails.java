package karan.com.popularmovies1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Karan on 25-02-2016.
 */
public class MovieDetails extends AppCompatActivity{

    private TextView releaseDate,ratings,description;
    private ImageView posterImage;
    private String PARCEL_KEY = "movieItem";
    private String TAG = "MoviesDetails";
    private MovieUtils movieUtils;
    private static String BASE_IMAGE_URL= "http://image.tmdb.org/t/p/w342/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_details);


        Log.d(TAG, "URL ");

        Intent i = getIntent();
        if(i != null){
            movieUtils= i.getParcelableExtra(PARCEL_KEY);
        }else{
            movieUtils = null;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMoviesDetials);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        posterImage = (ImageView) findViewById(R.id.detialsPoster);
        releaseDate = (TextView) findViewById(R.id.detailsReleaseDate);
        ratings = (TextView) findViewById(R.id.detailsRatings);
        description = (TextView) findViewById(R.id.detailsDescription);



        if(movieUtils != null) {
            getSupportActionBar().setTitle(movieUtils.title);
            releaseDate.setText(movieUtils.releaseDate);
            ratings.setText(movieUtils.voteAverage);
            description.setText(movieUtils.overView);
            Log.d(TAG, "URL "+ BASE_IMAGE_URL + movieUtils.posterPath);
            Picasso.with(MovieDetails.this).load(BASE_IMAGE_URL + movieUtils.posterPath).into(posterImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            // Do stuff
            finish();
        }
        return true;
    }

}
