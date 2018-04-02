package com.example.tomaszkot.popularmovies.Utilitis;

import android.text.TextUtils;
import android.util.Log;

import com.example.tomaszkot.popularmovies.models.Movie;
import com.example.tomaszkot.popularmovies.models.Review;
import com.example.tomaszkot.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * methods for JSON parse
 */

public class JSONUtilis{
    private static final String RESOULTS_KEY = "results";
    private static final String TITLE_KEY = "original_title";
    private static final String RELEASE_KEY = "release_date";
    private static final String POSTER_KEY = "poster_path";
    private static final String VOTE_KEY = "vote_average";
    private static final String PLOT_KEY = "overview";
    private static final String ID_KEY = "id";

    // ********************************************************************************************* Movie
    public static List<Movie> parseMovieJson(String json) throws JSONException {

        // Create instance of Movie Class
        List<Movie> movies = new ArrayList<>();

        // Create JSON Object
        JSONObject jsonObject = new JSONObject(json);

        if(TextUtils.isEmpty(json)){
            return null;
        }

        if(jsonObject.has(RESOULTS_KEY)) {
            JSONArray jsonResults = jsonObject.getJSONArray(RESOULTS_KEY);
            for (int i = 0; i < jsonResults.length(); i++) {
                JSONObject movieData = jsonResults.getJSONObject(i);
                Movie movie = new Movie();

                // --------------------------------------------------------------------- Movie Title
                String title = movieData.optString(TITLE_KEY);
                movie.setTitle(title);

                // ---------------------------------------------------------------- releaseDate Date
                String releaseDate = movieData.optString(RELEASE_KEY);
                movie.setReleaseDate(releaseDate);

                // --------------------------------------------------------------------- Poster Path
                String posterPath = movieData.optString(POSTER_KEY);
                movie.setPosterPath(posterPath);

                // ------------------------------------------------------------------- Vote Avegrade
                String voteAvegrade = movieData.optString(VOTE_KEY);
                movie.setVoteAverage(voteAvegrade);

                // ------------------------------------------------------------------- Plot Synopsis
                String plotSynopsis = movieData.optString(PLOT_KEY);
                movie.setPlotSynopsis(plotSynopsis);

                // ------------------------------------------------------------------------ movie ID
                int id = movieData.optInt(ID_KEY);
                movie.setID(id);

                movies.add(movie);
            }
        }
        return movies;
    }

    // ********************************************************************************************* Trailer
    public static List<Trailer> parseTrailerJson(String trailerJson)
            throws JSONException {

        List<Trailer> trailerList = new ArrayList<>();
        JSONObject jsonTrailerObj = new JSONObject(trailerJson);

        if(TextUtils.isEmpty(trailerJson)){
            return null;
        }

        if(jsonTrailerObj.has(RESOULTS_KEY)) {
            JSONArray trailersJson = jsonTrailerObj.getJSONArray(RESOULTS_KEY);
            for (int i = 0; i < trailersJson.length(); i++) {
                JSONObject trailerData = trailersJson.getJSONObject(i);
                Trailer trailer = new Trailer();

                // ------------------------------------------------------------------------------ ID
                String id = trailerData.optString("id");
                trailer.setId(id);

                // ----------------------------------------------------------------------------- Key
                String key = trailerData.optString("key");
                trailer.setKey(key);

                // ---------------------------------------------------------------------------- Name
                String name = trailerData.optString("name");
                trailer.setName(name);

                // ---------------------------------------------------------------------------- Site
                String site = trailerData.optString("site");
                trailer.setSite(site);

                // ---------------------------------------------------------------------------- Size
                String size = trailerData.optString("size");
                trailer.setSite(size);

                // ---------------------------------------------------------------------------- Type
                String type = trailerData.optString("type");
                trailer.setTYPE(type);

                Log.d("JSONTestTrailer" , "add: " + trailer.getId());
                trailerList.add(trailer);
            }
        }
        return trailerList;
    }


    // ********************************************************************************************* Review
    public static List<Review> parseReviewJson(String reviewJson)
            throws JSONException {
        List<Review> reviewList = new ArrayList<>();
        JSONObject jsonReviewObj = new JSONObject(reviewJson);

        if (TextUtils.isEmpty(reviewJson)) {
            return null;
        }

        if (jsonReviewObj.has(RESOULTS_KEY)) {
            JSONArray trailersJson = jsonReviewObj.getJSONArray(RESOULTS_KEY);
            for (int i = 0; i < trailersJson.length(); i++) {
                JSONObject reviewData = trailersJson.getJSONObject(i);
                Review review = new Review();

                // ------------------------------------------------------------------------------ ID
                String id = reviewData.optString("id");
                review.setId(id);

                // -------------------------------------------------------------------------- Author
                String author = reviewData.optString("author");
                review.setAuthor(author);

                // ------------------------------------------------------------------------- Content
                String content = reviewData.optString("content");
                review.setContent(content);

                reviewList.add(review);
            }
        }
        return reviewList;
    }
}