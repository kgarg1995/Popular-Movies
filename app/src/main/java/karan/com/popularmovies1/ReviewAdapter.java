package karan.com.popularmovies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Karan on 02-04-2016.
 */
public class ReviewAdapter extends
        RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static String TAG = "ReviewAdapter";
    private static Context mContext;
    private static ArrayList<ReviewUtils> Reviewlist;


    public ReviewAdapter(Context context, ArrayList<ReviewUtils> Reviewlist) {
        ReviewAdapter.mContext = context;
        ReviewAdapter.Reviewlist = Reviewlist;
    }

    @Override
    public int getItemCount() {
        return Reviewlist.size();
    }


    @Override
    public void onBindViewHolder(final ReviewViewHolder ReviewViewHolder, int i) {

        ReviewUtils ReviewUtils= Reviewlist.get(i);
        ReviewViewHolder.reviewAuthor.setText(ReviewUtils.author);
        ReviewViewHolder.reviewContent.setText(ReviewUtils.content);

    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;

        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.review_item, viewGroup, false);
        return new ReviewViewHolder(itemView);


    }




    public static class ReviewViewHolder extends RecyclerView.ViewHolder{

        protected TextView reviewAuthor, reviewContent;

        public ReviewViewHolder(View v){
            super(v);

            reviewAuthor = (TextView) v.findViewById(R.id.reviewItemAuthor);
            reviewContent = (TextView) v.findViewById(R.id.reviewItemContent);

        }
    }


}