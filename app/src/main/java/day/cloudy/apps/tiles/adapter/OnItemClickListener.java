package day.cloudy.apps.tiles.adapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Gaelan Bolger on 9/11/2016.
 * Generic item click listener for RecyclerView
 */
public interface OnItemClickListener<T> {

    void onItemClick(RecyclerView.ViewHolder holder, T item);

}
