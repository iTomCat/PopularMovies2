package com.example.tomaszkot.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.tomaszkot.popularmovies.Utilitis.JSONUtilis;
import com.example.tomaszkot.popularmovies.Utilitis.NetworkUtils;
import com.example.tomaszkot.popularmovies.data.MovieContentProvider;
import com.example.tomaszkot.popularmovies.data.MovieContract;
import com.example.tomaszkot.popularmovies.models.Movie;

import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,
        MovieAdapter.MovieAdapterOnClickHandler {

    private MenuOnTop tabs;
    private ImageView noInternetIcon;
    private SharedPreferences preferences;

    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private List<Movie> mMovieList = null;

    public static final String SEARCH_QUERY_URL_EXTRA = "query";
    private final static int ID_SEARCH_LOADER = 24;
    public static final String INT_MOVIE = "selected_movie";

    static final String LIST_POS = "position";
    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    public static final int FAVORITES = 2;
    public static final String SORT_ORDER = "sort";
    static int selectedSort;
    private int listPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listPos = savedInstanceState.getInt(LIST_POS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies(selectedSort);
    }

    // ********************************************************************************************* Init
    private void initialization(){
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // ----------------------------------------------------------------------- RecyclerView Init
        mRecyclerView = findViewById(R.id.recyclerview_forecast);
        int numberOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        //  ---------------------------------------------------------------------------- Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectedSort = preferences.getInt(getResources().getString(R.string.selected_tab),
                MainActivity.POPULAR);

        // -------------------------------------------- Menu on TOP: POPULAR / TOP RATED / FAVORITES
        View tabButtons = findViewById(R.id.belt_tab);
        assert tabButtons != null;

        RelativeLayout menuPopular = tabButtons.findViewById(R.id.button_popular);
        menuPopular.setClickable(true);
        menuPopular.setOnClickListener(new menuListener());
        menuPopular.setTag(POPULAR);

        RelativeLayout menuTopRated = tabButtons.findViewById(R.id.button_top_rated);
        menuTopRated.setClickable(true);
        menuTopRated.setOnClickListener(new menuListener());
        menuTopRated.setTag(TOP_RATED);

        RelativeLayout menuFavorites = tabButtons.findViewById(R.id.button_favorites);
        menuFavorites.setClickable(true);
        menuFavorites.setOnClickListener(new menuListener());
        menuFavorites.setTag(FAVORITES);

        tabs = new MenuOnTop(tabButtons, this);

        // -------------------------------------------------- Error Message (No internet connection)
        noInternetIcon = findViewById(R.id.no_internet_conn);
        noInternetIcon.setVisibility(View.INVISIBLE);
    }

    //**********************************************************************************************  Menu on top POPULAR / TOP RATED / FAVORITES Listener
    class menuListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int buttonTag = (int) v.getTag();
            listPos = 0;
            selectedSort = buttonTag;
            loadMovies(selectedSort);

            // ------------------------------------------------------------- Save Selected SortOrder
            preferences.edit().putInt(getResources()
                    .getString(R.string.selected_tab), buttonTag).apply();
        }
    }

    // ********************************************************************************************* OnClick List
    @Override
    public void onClick(int position) {
        startDetailMoviesActivity(position);
    }

    // ----------------------------------------------------------------- Start Detail Movie Activity
    private void startDetailMoviesActivity(int position){
        Movie selectedMovie =  mMovieList.get(position);

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(INT_MOVIE, selectedMovie);
        startActivity(intent);
    }

    // ********************************************************************************************* Start Load Movies
    public void loadMovies(int sortOrder){
        tabs.setSelectedTab(sortOrder);

        clearData();

        if ((selectedSort == POPULAR) || (selectedSort == TOP_RATED)) {
            if (NetworkUtils.checkInternetConnect(this)) { // ------------------------------- Check Internet Connect
                noInternetIcon.setVisibility(View.INVISIBLE);
                makeMovieSearchQuery(sortOrder);
            } else {
                noInternetIcon.setVisibility(View.VISIBLE);
            }
        }

        if (selectedSort == FAVORITES){
            noInternetIcon.setVisibility(View.INVISIBLE);
            makeMovieSearchQuery(sortOrder);
        }
    }

    public void clearData() {
        if (mMovieList != null) {
            mMovieList.clear();
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    // ********************************************************************************************* Start Load
    private void makeMovieSearchQuery(int sortOrder) {
            Bundle queryBundle = new Bundle();
            queryBundle.putInt(SEARCH_QUERY_URL_EXTRA, sortOrder);
            queryBundle.putInt(SORT_ORDER, sortOrder);
            LoaderManager loaderManager = getSupportLoaderManager();
            Object movieSearchLoader = loaderManager.getLoader(ID_SEARCH_LOADER);

        if (movieSearchLoader == null){
            loaderManager.initLoader(ID_SEARCH_LOADER, queryBundle, this);
        }else {
            loaderManager.restartLoader(ID_SEARCH_LOADER, queryBundle, this);
        }
    }

    // ********************************************************************************************* Async Task Loader methods
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        int currSortOrder = args.getInt(SORT_ORDER);

        // ---------------------------------------------------------------- Load POPULAR / TOP RATED
        if ((NetworkUtils.checkInternetConnect(this)
                && (currSortOrder == POPULAR) || (currSortOrder == TOP_RATED))) {
            return new MovieLoader(this, args.getInt(SEARCH_QUERY_URL_EXTRA));

        // -------------------------------------------------------------------------- Load FAVORITES
        } else {
            return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,
                    null, null, null, null);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object moviesData) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if ((selectedSort == POPULAR) || (selectedSort == TOP_RATED)) {
            mMovieAdapter.setMovieData((List<Movie>) moviesData);
            mMovieList = (List<Movie>) moviesData;
        } else if (selectedSort == FAVORITES){
            Cursor mFavoritesData = (Cursor) moviesData;
            mMovieList = MovieContentProvider.movieListFromCursor(mFavoritesData);
            mMovieAdapter.setMovieData(mMovieList);
        }

        if (listPos == 0) {
            mRecyclerView.smoothScrollToPosition(listPos);
        } else {
            mRecyclerView.scrollToPosition(listPos);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    // ********************************************************************************************* Async Task Loader Class for POPULAR & TOP RATED
    static class MovieLoader extends AsyncTaskLoader<List<Movie>> {
        private int sortOrder;

        MovieLoader(Context context, int sortOrder) {
            super(context);
            this.sortOrder = sortOrder;
        }

        @Override
        protected void onStartLoading(){
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public List<Movie> loadInBackground() {
            String dataFromMovieDB = NetworkUtils.getDataFromMoveDB(sortOrder);
            List<Movie> moviesList = null;

            if (dataFromMovieDB != null) {
                try {
                    moviesList = JSONUtilis.parseMovieJson(dataFromMovieDB);
                    Log.d("dataTest", "Data from MovieDB: " + dataFromMovieDB);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                return null;
            }
            return moviesList;
        }
    }

    // ********************************************************************************************* Save Instance State
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // --------------------------------------------------------------- Save Movies list position
        LinearLayoutManager myLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
        savedInstanceState.putInt(LIST_POS, scrollPosition);
    }
}
