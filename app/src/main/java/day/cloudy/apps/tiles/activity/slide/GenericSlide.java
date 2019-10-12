package day.cloudy.apps.tiles.activity.slide;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import day.cloudy.apps.tiles.R;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 12/26/2016.
 * Intro slide for displaying a generic image
 */
public class GenericSlide extends Fragment {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_BLURB = "blurb";
    private static final String EXTRA_IMAGE_RES_ID = "image_res_id";

    @BindView(R.id.text_view_slide_title)
    TextView vTitle;
    @BindView(R.id.text_view_slide_blurb)
    TextView vBlurb;
    @BindView(R.id.image_view_slide_image)
    ImageView vImage;

    private Drawable mDrawable;

    public static GenericSlide newInstance(String title, String blurb, int imageResId) {
        GenericSlide slide = new GenericSlide();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_BLURB, blurb);
        args.putInt(EXTRA_IMAGE_RES_ID, imageResId);
        slide.setArguments(args);
        return slide;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_base, container, false);
        inflater.inflate(R.layout.slide_conent_generic, view.findViewById(R.id.frame_bottom), true);
        bind(this, view);

        vTitle.setText(getArguments().getString(EXTRA_TITLE));
        vBlurb.setText(getArguments().getString(EXTRA_BLURB));

        mDrawable = ResourcesCompat.getDrawable(getResources(), getArguments().getInt(EXTRA_IMAGE_RES_ID), null);
        vImage.setImageDrawable(mDrawable);
        return view;
    }

    public void startAnimations() {
        if (mDrawable instanceof AnimatedVectorDrawable)
            vImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((AnimatedVectorDrawable) mDrawable).start();
                }
            }, 200);
    }
}
