package karan.com.popularmovies1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Karan on 25-02-2016.
 */
public class MovieDetails extends AppCompatActivity{

    private TextView releaseDate,ratings,description;
    private ImageView posterImage;
    private static LinearLayout trialerLayout;
    private String PARCEL_KEY = "movieItem";
    private String TAG = "MoviesDetails";
    private MovieUtils movieUtils;
    private static String TAG_RESULT = "results";
    private static String TAG_NAME = "name";
    private static String TAG_KEY= "key";
    private String BASE_IMAGE_URL= "http://image.tmdb.org/t/p/w342/";
    private String BASE_YOUTUBE_URL="https://youtu.be/";
    private String BASE_TRAILER_URL = "http://api.themoviedb.org/3/movie/";
    private DBHandler dbHandler;

    private int flag = 0;

    private OkHttpClient client;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_details);

        dbHandler = new DBHandler(MovieDetails.this);

        Intent i = getIntent();
        if(i != null){
            movieUtils= i.getParcelableExtra(PARCEL_KEY);
        }else{
            movieUtils = null;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMoviesDetials);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new OkHttpClient();

        posterImage = (ImageView) findViewById(R.id.detialsPoster);
        releaseDate = (TextView) findViewById(R.id.detailsReleaseDate);
        ratings = (TextView) findViewById(R.id.detailsRatings);
        description = (TextView) findViewById(R.id.detailsDescription);
        trialerLayout = (LinearLayout) findViewById(R.id.trailerLayout);
        trialerLayout.setVisibility(View.GONE);

        trialerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG , "Youtube URL " + BASE_YOUTUBE_URL);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_YOUTUBE_URL)));
            }
        });

        if(movieUtils != null) {
            getSupportActionBar().setTitle(movieUtils.title);
            releaseDate.setText(movieUtils.releaseDate);
            ratings.setText(movieUtils.voteAverage);
            description.setText(movieUtils.overView);
            Log.d(TAG, "URL "+ BASE_IMAGE_URL + movieUtils.posterPath);
            Picasso.with(MovieDetails.this).load(BASE_IMAGE_URL + movieUtils.posterPath).into(posterImage);
            FetchTrailerKey fetchTrailerKey =  new FetchTrailerKey();
            fetchTrailerKey.execute(BASE_TRAILER_URL  + movieUtils.id  + "/videos?api_key=36d9e05a1700874f1a755d3c95b0d6e8");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        if(movieUtils != null) {
            if(dbHandler.getData(Integer.parseInt(movieUtils.id)) != null){
                menu.findItem(R.id.actionFavorites).setIcon(R.drawable.action_favorite_filled);
                flag=1;
            }

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            // Do stuff
            finish();
        }

        if(itemId == R.id.actionFavorites){
            //TODO save to favorite db and change icon


            if(movieUtils != null) {
                if(flag == 0) {
                    dbHandler.addFavrotite(movieUtils);
                    item.setIcon(R.drawable.action_favorite_filled);
                }else{
                    dbHandler.deleteFavorite(Integer.parseInt(movieUtils.id));
                    item.setIcon(R.drawable.action_favorite);
                }
            }
        }
        return true;
    }

    String run(String url) throws IOException {

        Log.d(TAG, "URL "+ url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private class FetchTrailerKey extends AsyncTask<String , String , String> {

        String JSONResult = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                JSONResult = run(params[0]);
            }catch (IOException e){
                Log.e(TAG , "OkHTTP error " + e.getMessage());
                JSONResult = null;
            }
            return JSONResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result != null){
                try {
                    Log.d(TAG, "result "+ result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultArray =  jsonObject.getJSONArray(TAG_RESULT);
                    for(int i =0; i<resultArray.length() ;i++){
                        JSONObject arrayObject = resultArray.getJSONObject(i);
                        String name = arrayObject.getString(TAG_NAME);
                        if(name.equals("Trailer")){
                            BASE_YOUTUBE_URL = BASE_YOUTUBE_URL + arrayObject.getString(TAG_KEY);
                            Log.d(TAG , "Youtube URL " + BASE_YOUTUBE_URL);
                            trialerLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG , "JSON error " + e.getMessage());
                }

            }

        }
    }

}
