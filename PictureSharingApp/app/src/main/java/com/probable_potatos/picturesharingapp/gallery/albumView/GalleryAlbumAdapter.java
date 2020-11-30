package com.probable_potatos.picturesharingapp.gallery.albumView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.probable_potatos.picturesharingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dong on 29/11/2017.
 */

public class GalleryAlbumAdapter extends RecyclerView.Adapter<GalleryAlbumAdapter.FolderViewHolder> {

    Context context;

    List<Folder> folders;


    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView folder_name;
        TextView folder_count;
        ImageView folder_picture;

        FolderViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.galleryCardView);
            folder_name = (TextView)itemView.findViewById(R.id.folderName);
            folder_count = (TextView)itemView.findViewById(R.id.picNumber);
            folder_picture = (ImageView)itemView.findViewById(R.id.folderPic);
        }
    }



    public GalleryAlbumAdapter(Context context, List<Folder> folders){
        this.context = context;
        this.folders = folders;
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_gallery_album_card_view, viewGroup, false);
        FolderViewHolder folder_view_holder = new FolderViewHolder(v);
        return folder_view_holder;
    }


    @Override
    public void onBindViewHolder(FolderViewHolder personViewHolder, int i) {
        personViewHolder.folder_name.setText(folders.get(i).name);
        personViewHolder.folder_count.setText("" + folders.get(i).count);
        if (folders.get(i).imgUrl != null) {
            System.out.println("Using Picasso, URL: "+folders.get(i).imgUrl);
            Picasso.with(context)
                    .load(folders.get(i).imgUrl)
                    .error(new ColorDrawable(0))
                    .resize(300 ,300 )
                    .into(personViewHolder.folder_picture);
        } else {
            personViewHolder.folder_picture.setImageResource(folders.get(i).photoId);
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
