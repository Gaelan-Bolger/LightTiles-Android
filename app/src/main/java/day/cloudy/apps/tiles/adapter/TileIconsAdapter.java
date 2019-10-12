package day.cloudy.apps.tiles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.utils.CheatSheet;
import day.cloudy.apps.tiles.utils.ResourceUtils;

/**
 * Created by Gaelan Bolger on 12/24/2016.
 */
public class TileIconsAdapter extends RecyclerView.Adapter<TileIconsAdapter.Holder> {

    private final Context mContext;
    private final int[] mIcons;
    private OnItemClickListener<Integer> mListener;

    public TileIconsAdapter(Context context, int[] icons, OnItemClickListener<Integer> listener) {
        mContext = context;
        mIcons = icons;
        mListener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        int iconResId = getItem(position);
        holder.bind(iconResId);
        CheatSheet.setup(holder.itemView, ResourceUtils.getResourceName(mContext, iconResId));
        if (null != mListener)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(holder, getItem(holder.getAdapterPosition()));
                }
            });
    }

    @Override
    public int getItemCount() {
        return mIcons.length;
    }

    private int getItem(int position) {
        return mIcons[position];
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView icon;

        Holder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView;
        }

        void bind(int item) {
            icon.setImageResource(item);
        }
    }
}
