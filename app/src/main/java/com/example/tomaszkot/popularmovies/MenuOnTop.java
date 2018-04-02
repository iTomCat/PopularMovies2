package com.example.tomaszkot.popularmovies;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

 /**
 * Menu on top - tabs display - POPULAR / TOP RATED / FAVORITES
 */

class MenuOnTop {
    private ImageView popularIndic;
    private ImageView topratedIndic;
    private ImageView favoritesIndic;
    private AppCompatActivity activity;

    MenuOnTop(View tabButtons, AppCompatActivity activity) {
        this.activity = activity;
        // ----------------------------------------------------------------------------------------- Indicators in Menu
        popularIndic = tabButtons.findViewById(R.id.indic_popular);
        topratedIndic = tabButtons.findViewById(R.id.indic_top_rated);
        favoritesIndic = tabButtons.findViewById(R.id.indic_favorites);

        setIndicatorColor();
    }

    // --------------------------------------------------------------------------------------------  Change Alpha on Tabs
    void setSelectedTab(int tab){
        switch (tab) {
            case MainActivity.POPULAR:
                popularIndic.setAlpha(1.0f);
                topratedIndic.setAlpha(0.3f);
                favoritesIndic.setAlpha(0.3f);
                break;
            case MainActivity.TOP_RATED:
                popularIndic.setAlpha(0.3f);
                topratedIndic.setAlpha(1.0f);
                favoritesIndic.setAlpha(0.3f);
                break;
            case MainActivity.FAVORITES:
                popularIndic.setAlpha(0.3f);
                topratedIndic.setAlpha(0.3f);
                favoritesIndic.setAlpha(1.0f);
                break;
        }
    }

    // --------------------------------------------------------------------------------------------- Set Tab Colors
    private void setIndicatorColor(){
        int selColor = getColorFromReference(R.color.main_color_light);
        popularIndic.setBackgroundColor(selColor);
        topratedIndic.setBackgroundColor(selColor);
        favoritesIndic.setBackgroundColor(selColor);
    }

    private int getColorFromReference(int reference){
        return ContextCompat.getColor(activity.getApplicationContext(), reference);
    }
}
