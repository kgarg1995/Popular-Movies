package karan.com.popularmovies1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Karan on 03-04-2016.
 */
public class ListFragment extends Fragment{

    private GridView gridview;
    private ArrayList<MovieUtils> movieUtilses;
    private int CONNECTION_TIMOUT=10000;
    private String TAG = "MainActivity";

    private static final int LIST_LOADER = 0;

    private static String TAG_RESULTS = "results";
    private static String TAG_POSTER_PATH = "poster_path";
    private static String TAG_OVERVIEW = "overview";
    private static String TAG_TITLE = "title";
    private static String TAG_POPULARITY = "popularity";
    private static String TAG_VOTE_COUNT = "vote_count";
    private static String TAG_VOTE_AVERAGE = "vote_average";
    private static String TAG_ID = "id";
    private String TAG_RELEASE_DATE = "release_date";
    private String KEY_SAVEDINSTANCE_DATA = "movieDataSet";
    private OkHttpClient client;
    private String PARCEL_KEY = "movieItem";

    private static String URL_POPULARITY = "http://api.themoviedb.org/3/discover/" +
            "movie?sort_by=popularity.desc&api_key=";
    private static String URL_RATINGS = "http://api.themoviedb.org/3/discover/" +
            "movie?sort_by=vote_average.desc&api_key=";

    private DBHandler dbHandler;

    private AppCompatActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();

        dbHandler = new DBHandler(activity);
        movieUtilses = new ArrayList<>();
        client = new OkHttpClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.listfragment, container, false);

        setHasOptionsMenu(true);

        gridview = (GridView) rootView.findViewById(R.id.gridviewListMovies);

        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_SAVEDINSTANCE_DATA)) {
            //NO DATA SAVED, FETCH FROM INTERNET
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute(URL_POPULARITY);
        } else {

            movieUtilses = savedInstanceState.getParcelableArrayList(KEY_SAVEDINSTANCE_DATA);
            if (movieUtilses != null) {
                gridview.setAdapter(new MoviesGridAdapter(activity, movieUtilses));
            } else {
                movieUtilses = dbHandler.getAllFavorites();
                if (movieUtilses.size() > 0) {
                    gridview.setAdapter(new MoviesGridAdapter(activity, movieUtilses));
                } else {
                    FetchMovies fetchMovies = new FetchMovies();
                    fetchMovies.execute(URL_POPULARITY);
                }
            }
        }


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(activity , MovieDetails.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(PARCEL_KEY, movieUtilses.get(position));
                intent.putExtras(mBundle);
                startActivity(intent);

            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main , menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.actionPoplularity) {
            Log.d(TAG , "Sorting by popularity");
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute(URL_POPULARITY);
            return true;
        }

        if (id == R.id.actionRatings) {
            Log.d(TAG , "Sorting by ratings");
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute(URL_RATINGS);
            return true;
        }

        if(id == R.id.actionFavoritesHome){
            Log.d(TAG , "Sorting the fav");
            movieUtilses = new ArrayList<>();
            movieUtilses = dbHandler.getAllFavorites();
            MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(activity, movieUtilses);
            gridview.setAdapter(moviesGridAdapter);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_SAVEDINSTANCE_DATA , movieUtilses);
        super.onSaveInstanceState(outState);

    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private class FetchMovies extends AsyncTask<String , String , String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return run(params[0]);
            }catch (IOException e){
                Log.d(TAG , "okhttp error " + e.getMessage());
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.d(TAG, "result "+ result);

                try {
                    JSONObject resultJSON = new JSONObject(result);
                    JSONArray resultJSONArray = resultJSON.getJSONArray(TAG_RESULTS);

                    movieUtilses = new ArrayList<>();

                    for(int i=0;i<resultJSONArray.length(); i++){

                        JSONObject movieItem = resultJSONArray.getJSONObject(i);
                        MovieUtils movieUtils = new MovieUtils();
                        movieUtils.posterPath = movieItem.getString(TAG_POSTER_PATH);
                        movieUtils.overView = movieItem.getString(TAG_OVERVIEW);
                        movieUtils.title = movieItem.getString(TAG_TITLE);
                        movieUtils.popularity = movieItem.getString(TAG_POPULARITY);
                        movieUtils.voteCount = movieItem.getString(TAG_VOTE_COUNT);
                        movieUtils.voteAverage = movieItem.getString(TAG_VOTE_AVERAGE);
                        movieUtils.releaseDate = movieItem.getString(TAG_RELEASE_DATE);
                        movieUtils.id = movieItem.getString(TAG_ID);

                        Log.d(TAG , "Title " + movieUtils.id);

                        movieUtilses.add(movieUtils);
                    }


                    MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(activity , movieUtilses);
                    gridview.setAdapter(moviesGridAdapter);

                } catch (Exception e) {
                    // Check log for errors
                    e.printStackTrace();
                }

            }
        }


    }


}
