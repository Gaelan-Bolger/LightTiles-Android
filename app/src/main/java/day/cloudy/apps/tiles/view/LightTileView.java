package day.cloudy.apps.tiles.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import day.cloudy.apps.tiles.R;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 12/26/2016.
 */
public class LightTileView extends CardView {

    @BindView(R.id.text_view_tile_label)
    TextView vLabel;
    @BindView(R.id.image_view_tile_icon)
    ImageView vIcon;

    public LightTileView(Context context) {
        this(context, null);
    }

    public LightTileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LightTileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.light_tile, this);
        bind(this);

        TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable d = ta.getDrawable(0);
        ta.recycle();
        setForeground(d);
        setRadius(0f);
        setUseCompatPadding(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        vLabel.setEnabled(enabled);
        vIcon.setEnabled(enabled);
    }

    public void setLabelText(String label) {
        vLabel.setText(label);
    }

    public void setIconDrawable(Drawable drawable) {
        vIcon.setImageDrawable(drawable);
    }
}
