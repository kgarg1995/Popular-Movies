package karan.com.popularmovies1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import okhttp3.OkHttpClient;

/**
 * Created by Karan on 25-02-2016.
 */
public class MovieDetails extends AppCompatActivity{

   private String TAG = "MoviesDetails";


    private OkHttpClient client;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_details);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailsFragment())
                    .commit();
        }

    }


}
