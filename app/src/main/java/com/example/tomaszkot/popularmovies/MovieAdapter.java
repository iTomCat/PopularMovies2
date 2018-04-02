package com.example.tomaszkot.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tomaszkot.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * RecyclerView adapter
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private List<Movie> mMoviesData;
    private Context mContext;

    private final MovieAdapterOnClickHandler mClickHandler;


    public interface MovieAdapterOnClickHandler {
        void onClick(int position);
    }

    MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mPosterImage;

        MovieAdapterViewHolder(View view) {
            super(view);
            mContext = view.getContext();
            mPosterImage = view.findViewById(R.id.movie_cover);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder forecastAdapterViewHolder, int position) {
        String posterPath = mMoviesData.get(position).getPosterPath();
        String completePosterPath = "http://image.tmdb.org/t/p/w185/" + posterPath;
        Picasso.with(mContext)
                .load(completePosterPath)
                // Error Image
                //.error(R.drawable.no_connection)
                .into(forecastAdapterViewHolder.mPosterImage);

    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.size();
    }

    void setMovieData(List<Movie> moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
