package com.probable_potatos.picturesharingapp.gallery.photoView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.probable_potatos.picturesharingapp.Gallery;
import com.probable_potatos.picturesharingapp.GalleryPhotoPage;
import com.probable_potatos.picturesharingapp.R;
import com.probable_potatos.picturesharingapp.gallery.RecyclerItemClickListener;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.ContentItem;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Item;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.TitleItem;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 29/11/2017.
 */

public class GalleryPhotoViewListAdapter extends ArrayAdapter{
    private final Context context;
    private List<Item> itemList;



    public GalleryPhotoViewListAdapter(Context context, List<Item> itemList) {
        super(context, -1, itemList);
        this.context = context;
        this.itemList = itemList;

    }


    @Override
    public View getView(int position, View oldView, ViewGroup parent)
    {
        View retView;

        if (isTitleItem(position))
        {
            retView = buildTitleItem(position, parent);
        }
        else {
            retView = buildContentItem(position, parent);
//            retView = buildContentItemToGrid(position, parent);
        }
        

        return retView;
    }

    private View buildContentItemToGrid(int position, ViewGroup parent) {
        View retView;

        retView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gallery_photo_grid_view, parent,false);

        ContentItem contentItem = (ContentItem) itemList.get(position);

        GridView gridView = (GridView) retView.findViewById(R.id.list_grid);

        ArrayAdapter adapter = new GalleryPhotoViewGridAdapter(this.context, contentItem.getPhotosList());


        gridView.setAdapter(adapter);

        return retView;
    }

    private View buildContentItem(int position, ViewGroup parent) {

        final int list_position = position;
        View retView;

        retView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gallery_photo_recycler_view, parent,false);

        final ContentItem contentItem = (ContentItem) itemList.get(position);

        RecyclerView recyclerView = (RecyclerView) retView.findViewById(R.id.list_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.context, ContentItem.CONTENT_SPAN);

        GalleryPhotoViewRecyclerAdapter adapter = new GalleryPhotoViewRecyclerAdapter(this.context, contentItem.getPhotosList());

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(adapter);




        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                System.out.println(list_position+"-"+ position);


                String[] a = {contentItem.getPhotosList().get(position).imgUrl};
                new ImageViewer.Builder(context, a)
                        .setStartPosition(0)
                        .show();


            }

            @Override public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));


        return retView;
    }

    private View buildTitleItem(int position, ViewGroup parent) {


        View retView;

        retView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gallery_photo_text_bar_view,parent,false);

        TextView textView = (TextView) retView.findViewById(R.id.barText);

        //should be the titleItem
        textView.setText( ((TitleItem) itemList.get(position)).getTitleText() );

        return retView;
    }

    private boolean isTitleItem(int position) {
        return itemList.get(position).getItemType() == Item.TITLE_ITEM_TYPE ? true : false;
    }


}
