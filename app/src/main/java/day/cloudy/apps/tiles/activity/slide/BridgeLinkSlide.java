package day.cloudy.apps.tiles.activity.slide;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.ISlidePolicy;

import butterknife.BindView;
import day.cloudy.apps.tiles.R;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 * Intro slide for Hue Bridge button push
 */
public class BridgeLinkSlide extends Fragment implements ISlidePolicy {

    public interface OnTimedOutListener {
        void onTimedOut();
    }

    private static final int TIMEOUT = 30;

    @BindView(R.id.text_view_slide_title)
    TextView vTitle;
    @BindView(R.id.text_view_slide_blurb)
    TextView vBlurb;
    @BindView(R.id.progress_bar)
    ProgressBar vProgress;

    private CountDownTimer mCountDownTimer;

    public static BridgeLinkSlide newInstance() {
        return new BridgeLinkSlide();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_base, container, false);
        inflater.inflate(R.layout.slide_content_bridge_link, view.findViewById(R.id.frame_bottom), true);
        bind(this, view);

        vTitle.setText(R.string.slide_link_bridge_title);
        vBlurb.setText(R.string.slide_link_bridge_blurb);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyTimer();
    }

    @Override
    public boolean isPolicyRespected() {
        return false; // advanced in activity
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Toast.makeText(getActivity(), R.string.slide_link_bridge_toast, Toast.LENGTH_SHORT).show();
    }

    public void startTimer(final OnTimedOutListener listener) {
        vProgress.setMax(TIMEOUT);
        vProgress.setProgress(TIMEOUT);
        mCountDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                vProgress.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                vProgress.setProgress(0);
                listener.onTimedOut();
                destroyTimer();
            }
        };
        mCountDownTimer.start();
    }

    public void stopTimer() {
        destroyTimer();
        vProgress.setProgress(0);
    }

    private void destroyTimer() {
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}
