package com.example.budgetapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CustomSpinnerAdapter extends BaseAdapter {
    private Context context;
    int spinnerCategoryImageResource[];
    private LayoutInflater inflater;

     CustomSpinnerAdapter(Context applicationContext, int[] mImageResource) {
        this.context = applicationContext;
        this.spinnerCategoryImageResource = mImageResource;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return spinnerCategoryImageResource.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.custom_spinner_catagory, null);
        ImageView icon = convertView.findViewById(R.id.imageViewSpinnerCategory);
        icon.setImageResource(spinnerCategoryImageResource[position]);
        return convertView;
    }
}
