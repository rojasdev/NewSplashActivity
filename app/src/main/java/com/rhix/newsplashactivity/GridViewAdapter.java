package com.rhix.newsplashactivity;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private List<FoodModel> itemList;
    private final LayoutInflater inflater;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public GridViewAdapter(FragmentActivity activity) {
        this.context = activity;
        this.inflater = LayoutInflater.from(activity);
    }
    // Method to set data for the adapter
    public void setData(List<FoodModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    // Method to update data dynamically
    public void updateData(List<FoodModel> newData) {
        if (itemList != null) {
            itemList.clear();  // Clear the existing data
            itemList.addAll(newData);  // Add new data to the data source
            notifyDataSetChanged();  // Notify adapter that data has changed
        }
    }

    // Set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Get the number of items in the adapter
    @Override
    public int getCount() {
        return itemList != null ? itemList.size() : 0;
    }

    // Get a specific item based on its position
    @Override
    public FoodModel getItem(int position) {
        return itemList != null ? itemList.get(position) : null;
    }

    // Get the item ID (can be the position)
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Inflate the item layout if it's not already inflated
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.food_list_item, parent, false);
            holder = new ViewHolder();
            holder.itemNameTextView = convertView.findViewById(R.id.caption);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data to the view
        FoodModel item = getItem(position);
        if (item != null) {
            holder.itemNameTextView.setText(item.getFoodName());
        }

        // Handle the item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    // Notify the listener when an item is clicked
                    onItemClickListener.onItemClick(getItem(position));
                }
            }
        });

        return convertView;
    }

    // ViewHolder pattern for view recycling
    private static class ViewHolder {
        TextView itemNameTextView;
    }

    // Interface for the item click listener
    public interface OnItemClickListener {
        void onItemClick(FoodModel food);
    }
}
