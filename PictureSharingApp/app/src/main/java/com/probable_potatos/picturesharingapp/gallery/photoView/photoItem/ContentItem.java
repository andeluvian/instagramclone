package com.probable_potatos.picturesharingapp.gallery.photoView.photoItem;

import java.util.List;

/**
 * Created by dong on 06/12/2017.
 */

public class ContentItem extends Item {

    public static int CONTENT_SPAN = 3;

    List<Photos> photosList;
    int position;

    public ContentItem(List<Photos> photosList) {
        this.photosList = photosList;
    }

    public List<Photos> getPhotosList() {
        return photosList;
    }

    public void setPhotosList(List<Photos> photosList) {
        this.photosList = photosList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getItemType() {
        return Item.CONTENT_ITEM_TYPE;
    }
}
