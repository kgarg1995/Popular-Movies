package karan.com.popularmovies1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridview;
    private ArrayList<MovieUtils> movieUtilses;
    private int CONNECTION_TIMOUT=10000;
    private String TAG = "MainActivity";

    private static String TAG_RESULTS = "results";
    private static String TAG_POSTER_PATH = "poster_path";
    private static String TAG_OVERVIEW = "overview";
    private static String TAG_TITLE = "title";
    private static String TAG_POPULARITY = "popularity";
    private static String TAG_VOTE_COUNT = "vote_count";
    private static String TAG_VOTE_AVERAGE = "vote_average";

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

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });


        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute(URL_POPULARITY);

    }

    synchronized private String openHttpConnection(String urlStr) {
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
    }


    private class FetchMovies extends AsyncTask<String , String , String>{

        @Override
        protected String doInBackground(String... params) {
            publishProgress(openHttpConnection(params[0]));
            return null;
        }

        @Override
        protected void onProgressUpdate(String... result) {
            if (result[0] != null) {
                Log.d(TAG, "result "+ result[0]);

                try {
                    JSONObject resultJSON = new JSONObject(result[0]);
                    JSONArray resultJSONArray = resultJSON.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<resultJSONArray.length(); i++){
                        JSONObject movieItem = resultJSONArray.getJSONObject(i);
                        MovieUtils movieUtils = new MovieUtils();
                        movieUtils.posterPath = movieItem.getString(TAG_POSTER_PATH);
                        movieUtils.overView = movieItem.getString(TAG_OVERVIEW);
                        movieUtils.title = movieItem.getString(TAG_TITLE);
                        movieUtils.popularity = movieItem.getString(TAG_POPULARITY);
                        movieUtils.voteCount = movieItem.getString(TAG_VOTE_COUNT);
                        movieUtils.voteAverage = movieItem.getString(TAG_VOTE_AVERAGE);

                        movieUtilses.add(movieUtils);
                    }

                    gridview.setAdapter(new MoviesGridAdapter(MainActivity.this,movieUtilses));

                } catch (Exception e) {
                    // Check log for errors
                    e.printStackTrace();
                }

            }
        }

    }



}
