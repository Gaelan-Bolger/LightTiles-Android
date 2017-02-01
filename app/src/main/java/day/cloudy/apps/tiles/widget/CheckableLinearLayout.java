package day.cloudy.apps.tiles.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * This is a simple wrapper for {@link android.widget.LinearLayout} that implements the {@link android.widget.Checkable}
 * interface by keeping an internal 'checked' state flag.
 * <p>
 * This can be used as the root view for a custom list item layout for
 * {@link android.widget.AbsListView} elements with a
 * {@link android.widget.AbsListView#setChoiceMode(int) choiceMode} set.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean mChecked = false;
    private ArrayList<Checkable> mCheckableViews;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();

            for (Checkable checkable : mCheckableViews) {
                checkable.setChecked(mChecked);
            }
        }
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCheckableViews = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            findCheckableViews(getChildAt(i));
        }
    }

    private void findCheckableViews(View view) {
        if (view instanceof Checkable)
            mCheckableViews.add((Checkable) view);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findCheckableViews(viewGroup);
            }
        }
    }
}