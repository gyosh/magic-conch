package com.wyvern.fun.magicconch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;

/**
 * Created by gyosh on 7/18/15.
 */
public class CategoryArrayAdapter extends ArrayAdapter<Category> {
    public CategoryArrayAdapter(Context context, ArrayList<Category> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        // see https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_row, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.category_text);
        text.setText(category.getName());

        return convertView;
    }

}
