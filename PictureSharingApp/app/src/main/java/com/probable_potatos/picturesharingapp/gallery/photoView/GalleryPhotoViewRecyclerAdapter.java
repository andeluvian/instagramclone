package com.probable_potatos.picturesharingapp.gallery.photoView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.probable_potatos.picturesharingapp.R;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Photos;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dong on 29/11/2017.
 */

public class GalleryPhotoViewRecyclerAdapter extends RecyclerView.Adapter<GalleryPhotoViewRecyclerAdapter.PhotoViewHolder> {

    List<Photos> photos;

    Context context;


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photo_picture;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photo_picture = (ImageView)itemView.findViewById(R.id.photoPic);
        }
    }




    public GalleryPhotoViewRecyclerAdapter(Context context, List<Photos> photos){
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_gallery_photo_image_view, viewGroup, false);
        PhotoViewHolder photo_view_holder = new PhotoViewHolder(v);
        return photo_view_holder;
    }


    @Override
    public void onBindViewHolder(PhotoViewHolder personViewHolder, int i) {
        if (photos.get(i).imgUrl != null) {
            System.out.println("Using Picasso, URL: "+photos.get(i).imgUrl);
            Picasso.with(context)
                    .load(photos.get(i).imgUrl)
                    .error(new ColorDrawable(0))
                    .resize(300 ,300 )
                    .into(personViewHolder.photo_picture);
        } else {
            personViewHolder.photo_picture.setImageResource(photos.get(i).photoId);
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
