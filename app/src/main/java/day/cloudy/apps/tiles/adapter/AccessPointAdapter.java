package day.cloudy.apps.tiles.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.philips.lighting.hue.sdk.PHAccessPoint;

import java.util.List;

import day.cloudy.apps.tiles.R;
import timber.log.Timber;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class AccessPointAdapter extends RecyclerView.Adapter<AccessPointAdapter.Holder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ACCESS_POINT = 1;

    private final Context mContext;
    private final OnItemClickListener<PHAccessPoint> mListener;
    private List<PHAccessPoint> mItems;

    public AccessPointAdapter(Context context, OnItemClickListener<PHAccessPoint> listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ACCESS_POINT;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER)
            return new HeaderHolder(LayoutInflater.from(mContext).inflate(R.layout.header_access_point, parent, false));
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_access_point, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                Timber.d("onBindViewHolder: Bind header");
                int count = getItemCount() - 1;
                Resources res = mContext.getResources();
                String headerText = res.getQuantityString(R.plurals.found_bridges, count, count);
                ((HeaderHolder) holder).bind(headerText);
                break;
            case TYPE_ACCESS_POINT:
                Timber.d("onBindViewHolder: Bind access point");
                holder.bind(getItem(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener)
                            mListener.onItemClick(holder, getItem(holder.getAdapterPosition()));
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return null != mItems ? mItems.size() + 1 : 0;
    }

    private PHAccessPoint getItem(int position) {
        return mItems.get(position - 1);
    }

    public void setItems(List<PHAccessPoint> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView text1;
        TextView text2;

        Holder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image_view_access_point_icon);
            text1 = itemView.findViewById(R.id.text_view_access_point_mac);
            text2 = itemView.findViewById(R.id.text_view_access_point_ip);
        }

        void bind(PHAccessPoint accessPoint) {
            icon.setImageResource(R.drawable.ic_phbridge_v2);
            text1.setText(accessPoint.getMacAddress());
            text2.setText(accessPoint.getIpAddress());
        }
    }

    private class HeaderHolder extends Holder {

        TextView text1;

        HeaderHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.text_view_access_point_header);
        }

        void bind(String headerText) {
            text1.setText(headerText);
        }
    }
}
