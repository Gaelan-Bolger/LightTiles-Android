package day.cloudy.apps.tiles.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.hue.HueUtils;

import static butterknife.ButterKnife.findById;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class PHLightAdapter extends RecyclerView.Adapter<PHLightAdapter.Holder> {

    private SparseBooleanArray mSelection = new SparseBooleanArray();
    private final Context mContext;
    private final OnItemClickListener<PHLight> mListener;
    private boolean mShowIcons;
    private boolean mIsMultiSelect;
    private List<PHLight> mLights;

    public PHLightAdapter(Context context, OnItemClickListener<PHLight> listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(getLayoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.bind(getLight(position));
        holder.icon.setVisibility(mShowIcons ? View.VISIBLE : View.GONE);
        if (mIsMultiSelect)
            ((Checkable) holder.itemView).setChecked(mSelection.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMultiSelect) {
                    toggleSelection(holder.getAdapterPosition());
                } else if (null != mListener) {
                    mListener.onItemClick(holder, getLight(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != mLights ? mLights.size() : 0;
    }

    private int getLayoutId() {
        return mIsMultiSelect ? R.layout.item_light_multi : R.layout.item_light_single;
    }

    private PHLight getLight(int position) {
        return mLights.get(position);
    }

    public void setShowIcons(boolean showIcons) {
        mShowIcons = showIcons;
        notifyDataSetChanged();
    }

    public void setIsMultiSelect(boolean isMultiSelect) {
        mIsMultiSelect = isMultiSelect;
        notifyDataSetChanged();
    }

    public void setLights(List<PHLight> lights) {
        mLights = lights;
        notifyDataSetChanged();
    }

    public List<PHLight> getSelection() {
        List<PHLight> selection = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++)
            if (mSelection.get(i))
                selection.add(getLight(i));
        return selection;
    }

    private void toggleSelection(int position) {
        if (mSelection.get(position))
            mSelection.delete(position);
        else
            mSelection.put(position, true);
        notifyItemChanged(position);
    }

    public void setLightSelected(String identifier) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getLight(i).getIdentifier().equals(identifier)) {
                toggleSelection(i);
                return;
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;

        Holder(View itemView) {
            super(itemView);
            icon = findById(itemView, R.id.image_view_light_icon);
            name = findById(itemView, R.id.text_view_light_name);
        }

        void bind(PHLight item) {
            icon.setImageResource(HueUtils.getLightIconResources(item.getModelNumber()));
            name.setText(item.getName());
        }
    }
}
