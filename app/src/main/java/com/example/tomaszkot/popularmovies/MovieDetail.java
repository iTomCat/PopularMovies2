package com.example.tomaszkot.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomaszkot.popularmovies.Utilitis.JSONUtilis;
import com.example.tomaszkot.popularmovies.Utilitis.NetworkUtils;
import com.example.tomaszkot.popularmovies.data.MovieContract;
import com.example.tomaszkot.popularmovies.models.Movie;
import com.example.tomaszkot.popularmovies.models.Review;
import com.example.tomaszkot.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity displaying the details of the film
 */

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks{
    private ImageView posterIv;
    private TextView voteTv;
    private TextView releaseTv;
    private TextView plotTv;
    Movie selectedMovie;
    private ImageView favoriteStar;
    private boolean movieIsFavorite;

    private List<Trailer> mTrailerList;
    private List<Review> mReviewList = null;

    private static final String MOVIE_QUERRY = "trailers_querry";
    private static final String REVIEW_QUERRY = "review_query";
    public final static int TRAILERS_LOADER = 89;
    public final static int REVIEWS_LOADER  = 66;
    public final static int FAVORITES_LOADER  = 96;
    public final static String REVIEW_DATA = "review_data";
    private int id;
    private int id_base;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        assert  getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initialization();

        selectedMovie = getIntent().getParcelableExtra(MainActivity.INT_MOVIE);
        if (selectedMovie == null) {
            closeOnError();
        }

        populateUI();
        loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ********************************************************************************************* Init
    private void initialization(){
        posterIv = findViewById(R.id.poster_image);
        voteTv = findViewById(R.id.vote);
        releaseTv = findViewById(R.id.release);
        plotTv = findViewById(R.id.plot);
        favoriteStar = findViewById(R.id.favorite_star);
        favoriteStar.setOnClickListener(new starClickListener());
    }

    // ********************************************************************************************* Populate UI
    private void populateUI(){
        // ----------------------------------------------------------------------------------- Title
        setTitle(selectedMovie.getTitle());

        // ----------------------------------------------------------------------------------- Image
        String posterPath = selectedMovie.getPosterPath();
        String completePosterPath = "http://image.tmdb.org/t/p/w185/" + posterPath;
        Picasso.with(this)
                .load(completePosterPath)
                // Error Image
                //.error(R.drawable.no_connection)
                .into(posterIv);

        // ----------------------------------------------------------------------------------- Texts
        String voteTxt = selectedMovie.getVoteAverage();
        voteTv.setText(voteTxt);

        String releaseTxt = selectedMovie.getReleaseDate();
        releaseTv.setText(releaseTxt);

        String plotTxt = selectedMovie.getPlotSynopsis();
        plotTv.setText(plotTxt);

        id = selectedMovie.getID();
    }

    // ********************************************************************************************* Close on error
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    // ********************************************************************************************* Start Load Trailers or Reviews
    public void loadData(){
        final ImageView noInternetIcon = findViewById(R.id.no_internet_conn);
        if(NetworkUtils.checkInternetConnect(this)){
            noInternetIcon.setVisibility(View.INVISIBLE);
            startLoader(id, TRAILERS_LOADER);
            startLoader(id, REVIEWS_LOADER);
            startLoader(id, FAVORITES_LOADER);
        }else if (MainActivity.selectedSort != MainActivity.FAVORITES){
            noInternetIcon.setVisibility(View.VISIBLE);
        }
    }

    // ********************************************************************************************* Load Movie Trailers
    private void startLoader(int id, int actLoader){
        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();

        if (actLoader == TRAILERS_LOADER){ // -------------------------------------- Trailers Loader
            queryBundle.putInt(MOVIE_QUERRY, id);

            Loader<Integer> trailerLoader = loaderManager.getLoader(TRAILERS_LOADER);

            if (trailerLoader == null){
                loaderManager.initLoader(TRAILERS_LOADER, queryBundle, this);
            }else {
                loaderManager.restartLoader(TRAILERS_LOADER, queryBundle, this);
            }

        }else if (actLoader == REVIEWS_LOADER){ // ---------------------------------- Reviews Loader
            queryBundle.putInt(REVIEW_QUERRY, id);
            Loader<Integer> reviewLoader = loaderManager.getLoader(REVIEWS_LOADER);

            if (reviewLoader == null){
                loaderManager.initLoader(REVIEWS_LOADER, queryBundle, this);
            }else {
                loaderManager.restartLoader(REVIEWS_LOADER, queryBundle, this);
            }

        }else if (actLoader == FAVORITES_LOADER) { // ----------------------------- Favorites Loader
            Loader<Integer> reviewLoader = loaderManager.getLoader(FAVORITES_LOADER);

            if (reviewLoader == null){
                loaderManager.initLoader(FAVORITES_LOADER, queryBundle, this);
            }else {
                loaderManager.restartLoader(FAVORITES_LOADER, queryBundle, this);
            }
        }
    }

    // ********************************************************************************************* Async Task Loader methods
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        if (id == TRAILERS_LOADER) {
            return new MovieLoader(this,  args.getInt(MOVIE_QUERRY));

        } else if (id == REVIEWS_LOADER){
            return new ReviewsLoader(this, args.getInt(REVIEW_QUERRY));

        } else if (id== FAVORITES_LOADER){
            String[] projection = {MovieContract.MovieEntry.COLUMN_ID, MovieContract.MovieEntry._ID};
            return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,
                    projection, null, null, null);
        }
        return null;
    }



    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int actLoader = loader.getId();
        if (actLoader == TRAILERS_LOADER) { // ---------------------------- Finish Load Trailer List
            mTrailerList = (List<Trailer>) data;
            addTrailersToLayout();

            // Invalidate Share Menu
            invalidateOptionsMenu();

        } else if (actLoader == REVIEWS_LOADER){  // ----------------------- Finish Load Review List
            mReviewList = (List<Review>) data;
            addRevievsToLayout();

        } else if (actLoader == FAVORITES_LOADER) { // ----------------------- Finish Load Favorites
            Cursor idFavorites = (Cursor) data;
            movieIsFavorite = ifMovieIsFavorites(idFavorites);
            setStarColor(movieIsFavorite);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }


    // ********************************************************************************************************************************
    // ********************************************************************************************* TRAILERS - Async Task Loader Class
    static class MovieLoader extends AsyncTaskLoader<List<Trailer>> {
        private int id;
        private List<Trailer> mTrailers;

        MovieLoader(Context context, int id) {
            super(context);
            this.id = id;
        }

        @Override
        protected void onStartLoading(){
            super.onStartLoading();

            if ( mTrailers != null) {
                deliverResult(mTrailers);
            } else {
                forceLoad();
            }
        }

        @Override
        public List<Trailer> loadInBackground() {
            if (id < 0) {
                return null;
            }

            try {
                URL detailDataUrl = NetworkUtils.buildUrlForDetailMovie(TRAILERS_LOADER, id);
                String trailersFromHttp = NetworkUtils.getResponseFromHttpUrl(detailDataUrl);
                List<Trailer> trailerList = new ArrayList<>();

                try {
                    trailerList = JSONUtilis.parseTrailerJson(trailersFromHttp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return trailerList;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        public void deliverResult(List<Trailer> trailers) {
            mTrailers = trailers;
            super.deliverResult(trailers);
        }

    }

    // ********************************************************************************************* Add trailers to View
    private void addTrailersToLayout(){
        int numberOfTrailers = 0;
        final ImageView divider = findViewById(R.id.divider2);
        final TextView title = findViewById(R.id.trailers_title);

        if (mTrailerList != null) numberOfTrailers = mTrailerList.size();

        if (numberOfTrailers > 0) {
            title.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE)
            ;
            final LinearLayout trailerLayout = findViewById(R.id.trailers_layout);

            final String IMAGE_YOUTUBE = "https://img.youtube.com/vi/";
            String endUrl = "/0.jpg";

            for (Trailer actTrailer : mTrailerList) {

                ImageView trailerView = new ImageView(this);
                Picasso.with(this)
                        .load(IMAGE_YOUTUBE
                                .concat(actTrailer.getKey())
                                .concat(endUrl))
                        .placeholder(R.drawable.movie)
                        .fit().into(trailerView);

                trailerView.setOnClickListener(new trailersClickListener());
                trailerView.setTag(mTrailerList.indexOf(actTrailer));

                int bottomMrigin = (int) getResources().getDimension(R.dimen.divide);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, bottomMrigin);

                trailerView.setLayoutParams(lp);
                trailerLayout.addView(trailerView);
            }

        }else { // ---------------------- No Trailers - remove Trailers descriptions from the layout
            divider.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }
    }


    //********************************************************************************************** Trailers ClickListener
    private class trailersClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int trailerTag = (int)v.getTag();

            String urlAsString = "https://www.youtube.com/watch?v=";
            Uri youTubePage = Uri.parse(urlAsString.concat(mTrailerList.get(trailerTag).getKey()));

            Intent intent = new Intent(Intent.ACTION_VIEW, youTubePage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    // *******************************************************************************************************************************
    // ********************************************************************************************* REVIEWS - Async Task Loader Class
    static class ReviewsLoader extends AsyncTaskLoader<List<Review>> {
        private List<Review> mReviewList;
        private int id;

        ReviewsLoader(Context context, int id) {
            super(context);
            this.id = id;
        }

        @Override
        protected void onStartLoading(){
            super.onStartLoading();

            if ( mReviewList != null) {
                deliverResult(mReviewList);
            } else {
                forceLoad();
            }
        }

        @Override
        public List<Review> loadInBackground() {
            if (id < 0) {
                return null;
            }

            try {
                URL detailDataUrl = NetworkUtils.buildUrlForDetailMovie(REVIEWS_LOADER, id);

                String reviewsFromHttp = NetworkUtils.getResponseFromHttpUrl(detailDataUrl);
                List<Review> reviewList = new ArrayList<>();

                try {
                    reviewList = JSONUtilis.parseReviewJson(reviewsFromHttp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return reviewList;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(List<Review> reviewList) {
            mReviewList = reviewList;
            super.deliverResult(reviewList);
        }
    }

    // ********************************************************************************************* Add Reviews to View
    private void addRevievsToLayout(){
        int numberOfReviews = 0;
        final ImageView divider = findViewById(R.id.divider1);
        final TextView title = findViewById(R.id.rewiews_title);

        if (mReviewList != null) {
            numberOfReviews = mReviewList.size();
        }

        if (numberOfReviews > 0) {
            final LinearLayout reviewsLayout = findViewById(R.id.reviews_layout);
            divider.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);

            for (Review actReview : mReviewList) {
                View reviewView = View.inflate(this, R.layout.review_item, null);
                TextView author = reviewView.findViewById(R.id.author_txt);
                author.setText(actReview.getAuthor());

                reviewView.setOnClickListener(new reviewsClickListener());
                reviewView.setTag(mReviewList.indexOf(actReview));
                reviewsLayout.addView(reviewView);
            }

        }else{ // ------------------------- No Reviews - remove Reviews descriptions from the layout
                divider.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
        }
    }

    //********************************************************************************************** Reviews ClickListener
    private class reviewsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int reviewTag = (int)v.getTag();

            ReviewDetail dialogAreYouSure = new ReviewDetail();
            Review selectedRev =  mReviewList.get(reviewTag);
            selectedRev.setTitle(selectedMovie.getTitle());

            Bundle bundle = new Bundle();
            bundle.putParcelable(REVIEW_DATA, selectedRev);
            dialogAreYouSure.setArguments(bundle);
            dialogAreYouSure.show(getSupportFragmentManager(), "rev_detail");
        }
    }

    //********************************************************************************************************
    //********************************************************************************************** Favorites
    private class starClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) { // --------------------------- Add to Favorites Click Listener
            movieIsFavorite = !movieIsFavorite;

            if(movieIsFavorite){
                addToFavorites();
            }else {
                removeFromFavorites(id_base);
            }

            setStarColor(movieIsFavorite);
            starAnimation();
        }
    }

    // ---------------------------------------------------------------------- Add movie to Favorites
    private void addToFavorites(){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, selectedMovie.getID());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, selectedMovie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, selectedMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, selectedMovie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE, selectedMovie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, selectedMovie.getPlotSynopsis());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if (uri != null)  showToastInfo(movieIsFavorite);
    }

    // ----------------------------------------------------------------- Remove movie from Favorites
    private void removeFromFavorites(int item){
        //int id = (int) viewHolder.itemView.getTag();
        String stringId = Integer.toString(item);
        Uri uri =  MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        getContentResolver().delete(uri, null, null);
        getSupportLoaderManager().restartLoader(FAVORITES_LOADER, null, this);
    }

    private void setStarColor (boolean isChceked){
        if (isChceked){
            favoriteStar.setColorFilter(getResources().getColor(R.color.main_color_light));
        }else{
            favoriteStar.setColorFilter(getResources().getColor(R.color.light_gray));
        }

    }

    private void starAnimation(){
        ImageView starAnimation = findViewById(R.id.animate_star);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.star_anim);
        starAnimation.setAlpha(1f);
        animation.setFillAfter(true);
        animation.setDuration(400);
        starAnimation.startAnimation(animation);
    }

    private void showToastInfo(boolean isChceked){
        String info;
        if (isChceked){
            info = getResources().getString(R.string.fav_checked);
        }else {
            info = getResources().getString(R.string.fav_unchecked);
        }
        Toast.makeText(this, info, Toast.LENGTH_LONG).show();
    }

    // ------------------------------------------------------- Checking if the movie is in favorites
    private boolean ifMovieIsFavorites(Cursor cursor){
        int currentId = selectedMovie.getID();
        int movie_id_column = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
           int movieId = cursor.getInt(movie_id_column);

           if (movieId == currentId){
               int _id = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
               id_base = cursor.getInt(_id);
               Log.d("DeleteTest", "_ID " + id_base);
               return true;

           }
        }
        return false;
    }

    // ****************************************************************************************************************
    // ********************************************************************************************* Menu Share Trailer
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int numberOfTrailers = -1;
        if (mTrailerList != null) numberOfTrailers = mTrailerList.size();
            if (numberOfTrailers > 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.share, menu);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                String urlAsString = "https://www.youtube.com/watch?v=";
                Uri youTubePage = Uri.parse(urlAsString.concat(mTrailerList.get(0).getKey()));
                shareURL(youTubePage.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareURL(String url) {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(getResources().getString(R.string.trailer_title))
                .setSubject(getResources().getString(R.string.trailer))
                .setText(url)
                .startChooser();
    }
}
