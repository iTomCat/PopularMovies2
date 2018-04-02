package com.example.tomaszkot.popularmovies;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomaszkot.popularmovies.Utilitis.Tools;
import com.example.tomaszkot.popularmovies.models.Review;

/**
 * Display of the selected review
 */

public class ReviewDetail extends DialogFragment {
    private View view;
    int style = DialogFragment.STYLE_NO_TITLE;
    int theme = R.style.dialog_style;
    Review actReview;

    ImageView background;

    // ********************************************************************************************* Create View & Dialog
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(style, theme);
        return super.onCreateDialog(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rewiew_display, container, true);

        // -------------------------------------------------------------------- Get data from Bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        actReview = bundle.getParcelable(MovieDetail.REVIEW_DATA);

        // ----------------------------------------------------------------------- Key Back Listener
        getDialog().setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });

        populateUI();
        setWindowSize();

        return view;
    }

    // ********************************************************************************************* Show Data
    private void populateUI(){
        background = view.findViewById(R.id.review_backgr);
        final TextView author = view.findViewById(R.id.author_name);
        final TextView title = view.findViewById(R.id.review_title);
        final TextView content = view.findViewById(R.id.review_content);

        if (actReview == null){
            return;
        }

        author.setText(actReview.getAuthor());
        title.setText(actReview.getTitle());
        content.setText(actReview.getContent());
    }

    // ********************************************************************************************* Set window size
    private void setWindowSize(){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        int screenWidth = Tools.getScreenWidth(activity);
        //int screenHeight = Tools.getScreenHeight(activity);

        //int dialogHeight = screenHeight - (int) getResources().getDimension(R.dimen.marigin_height);

        background.getLayoutParams().width = screenWidth - (int) getResources().getDimension(R.dimen.marigin_width);
        //background.getLayoutParams().height = dialogHeight;
    }
}
