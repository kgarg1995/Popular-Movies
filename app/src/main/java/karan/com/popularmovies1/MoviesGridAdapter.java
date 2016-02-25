package karan.com.popularmovies1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Karan on 25-02-2016.
 */
public class MoviesGridAdapter extends BaseAdapter {


    private ArrayList<MovieUtils> moviesList;
    private Context mContext;

    public MoviesGridAdapter(Context context , ArrayList<MovieUtils> moviesList){
        this.moviesList = moviesList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return moviesList.size();
    }

    @Override
    public Object getItem(int position) {
        return moviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder gridViewHolder;

        if(convertView == null) {

            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.movie_gridview_item, parent, false);

            // initialize the view holder
            gridViewHolder = new GridViewHolder();
            gridViewHolder.posterImage = (ImageView) convertView.findViewById(R.id.itemMoviePoster);
            convertView.setTag(gridViewHolder);
        } else {
            // recycle the already inflated view
            gridViewHolder = (GridViewHolder) convertView.getTag();
        }

        // update the item view
        MovieUtils movieUtils = moviesList.get(position);
        gridViewHolder.posterImage.setImageBitmap(movieUtils.moviePoster);

        return convertView;
    }

    private static class GridViewHolder{
        protected ImageView posterImage;
    }

}
