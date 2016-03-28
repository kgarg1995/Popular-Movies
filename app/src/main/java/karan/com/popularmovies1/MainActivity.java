package karan.com.popularmovies1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private GridView gridview;
    private ArrayList<MovieUtils> movieUtilses;
    private int CONNECTION_TIMOUT=10000;
    private String TAG = "MainActivity";
    private MoviesGridAdapter popularityMoviesGridAdapter,ratingsMovieGridAdapter;

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
            "movie?sort_by=popularity.desc&api_key=36d9e05a1700874f1a755d3c95b0d6e8";
    private static String URL_RATINGS = "http://api.themoviedb.org/3/discover/" +
            "movie?sort_by=vote_average.desc&api_key=36d9e05a1700874f1a755d3c95b0d6e8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieUtilses = new ArrayList<>();
        gridview = (GridView) findViewById(R.id.gridview);

        client = new OkHttpClient();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMainActivity);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movieDataSet")) {
            //NO DATA SAVED, FETCH FROM INTERNET
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute(URL_POPULARITY);
        } else {

            movieUtilses = savedInstanceState.getParcelableArrayList(KEY_SAVEDINSTANCE_DATA);
            if (movieUtilses != null) {
                gridview.setAdapter(new MoviesGridAdapter(MainActivity.this, movieUtilses));
            } else {
                FetchMovies fetchMovies = new FetchMovies();
                fetchMovies.execute(URL_POPULARITY);
            }
        }




        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(MainActivity.this , MovieDetails.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(PARCEL_KEY, movieUtilses.get(position));
                intent.putExtras(mBundle);
                startActivity(intent);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_SAVEDINSTANCE_DATA , movieUtilses);
        super.onSaveInstanceState(outState);

    }

    /*synchronized private String openHttpConnection(String urlStr) {
        InputStream in = null;
        int resCode = -1;
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setConnectTimeout(CONNECTION_TIMOUT);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String line;

                // read from the urlconnection via the bufferedreader
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                }
                bufferedReader.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }*/

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private class FetchMovies extends AsyncTask<String , String , String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                publishProgress(run(params[0]));
            }catch (IOException e){
                Log.d(TAG , "okhttp error " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... result) {
            if (result[0] != null) {
                Log.d(TAG, "result "+ result[0]);

                try {
                    JSONObject resultJSON = new JSONObject(result[0]);
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


                    MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(MainActivity.this , movieUtilses);
                    gridview.setAdapter(moviesGridAdapter);

                } catch (Exception e) {
                    // Check log for errors
                    e.printStackTrace();
                }

            }
        }

    }



}
