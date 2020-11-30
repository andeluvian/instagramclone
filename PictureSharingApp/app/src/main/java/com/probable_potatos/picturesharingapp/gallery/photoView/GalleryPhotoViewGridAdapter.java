package com.probable_potatos.picturesharingapp.gallery.photoView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.probable_potatos.picturesharingapp.R;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Photos;

import java.util.List;

/**
 * Created by dong on 06/12/2017.
 */

public class GalleryPhotoViewGridAdapter extends ArrayAdapter{

    Context context;
    List<Photos> photoList;

    public GalleryPhotoViewGridAdapter(@NonNull Context context,  @NonNull List<Photos> photoList) {
        super(context, -1, photoList);
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        System.out.println("grid Count: " + photoList.size());
        return photoList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View retView;

        retView = LayoutInflater.from(this.context).inflate(R.layout.activity_gallery_photo_image_view, parent,false);

        ImageView pic = (ImageView) retView.findViewById(R.id.photoPic);

        pic.setImageResource(photoList.get(position).photoId);


        return retView;
    }
}
