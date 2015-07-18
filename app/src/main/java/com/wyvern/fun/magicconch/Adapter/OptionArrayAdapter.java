package com.wyvern.fun.magicconch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.Model.Option;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;

/**
 * Created by gyosh on 7/18/15.
 */
public class OptionArrayAdapter extends ArrayAdapter<Option>{
    public OptionArrayAdapter(Context context, ArrayList<Option> options) {
        super(context, 0, options);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Option option = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        // see https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.option_row, parent, false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.option_enabled_checkbox);
        TextView text = (TextView) convertView.findViewById(R.id.option_text);


        img.setImageResource(getAppropriateImage(option));
        text.setText(option.getName());

        return convertView;
    }

    private int getAppropriateImage(Option option) {
        int ret;

        if (isAddButton(option)){
            ret = android.R.drawable.ic_input_add;
        }else {
            if (option.isEnabled()) {
                ret = android.R.drawable.checkbox_on_background;
            } else {
                ret = android.R.drawable.checkbox_off_background;
            }
        }
        return ret;
    }

    private boolean isAddButton(Option option) {
        return option.getId() < 0;
    }
}
