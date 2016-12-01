package com.example.ajs.LietuvaGO;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<CustomItem> implements View.OnClickListener {

    private ArrayList<CustomItem> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView description;
        ImageView image;
    }

    public CustomAdapter(ArrayList<CustomItem> data, Context context) {
        super(context, R.layout.simple_list_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        Log.e("view",v.toString());
//        TextView txView = (TextView) v.findViewById(R.id.itemDescription);
//        if (txView.getVisibility() == View.INVISIBLE) {
//            txView.setVisibility(View.VISIBLE);
//        } else {
//            txView.setVisibility(View.INVISIBLE);
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CustomItem dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.simple_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.itemName);
            viewHolder.description = (TextView) convertView.findViewById(R.id.itemDescription);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.itemImageView);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.name);

        int len = dataModel.descritpion.length() < 100 ? 0: 100;

        dataModel.descritpion = dataModel.descritpion.substring(0, len);
        viewHolder.description.setText(Html.fromHtml(dataModel.descritpion));

        Glide.with(getContext()).load(dataModel.image).into(viewHolder.image);

        //result.setOnClickListener(this);
        //Log.e();
        // Return the completed view to render on screen
        return convertView;
    }
}