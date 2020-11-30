package com.probable_potatos.picturesharingapp;

/**
 * Created by kaisti on 21/11/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GridAdapter extends BaseAdapter{

    String [] result;
    Context context;
    int [] imageId;

    private static LayoutInflater inflater=null;
    public GridAdapter(MainActivity mainActivity, String[] GridItems, int[] GridImages) {

        result=GridItems;
        context=mainActivity;
        imageId=GridImages;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView grid_text;
        ImageView grid_img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.single_gridview, null);
        holder.grid_text =(TextView) rowView.findViewById(R.id.grid_texts);
        holder.grid_img =(ImageView) rowView.findViewById(R.id.grid_images);

        holder.grid_text.setText(result[position]);
        holder.grid_img.setImageResource(imageId[position]);

        return rowView;
    }

}