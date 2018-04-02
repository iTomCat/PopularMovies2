package com.example.tomaszkot.popularmovies.Utilitis;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Methods needed to display
 */

public class Tools {

    // ********************************************************************************************* Convert Dp to Px
    @SuppressWarnings("unused")
    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (Math.round(px));
    }

    @SuppressWarnings("unused")
    // ********************************************************************************************* Convert Px to Dp
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    // ********************************************************************************************* Screen Width
    public static int getScreenWidth(AppCompatActivity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @SuppressWarnings("unused")
    // ********************************************************************************************* Screen Height
    public static int getScreenHeight(AppCompatActivity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
