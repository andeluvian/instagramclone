package com.probable_potatos.picturesharingapp.gallery.photoView.photoItem;

/**
 * Created by dong on 06/12/2017.
 */

public abstract class Item {
    public static int TITLE_ITEM_TYPE = 0;
    public static int CONTENT_ITEM_TYPE = 1;

//    public int itemType = TITLE_ITEM_TYPE;

    public abstract int getItemType();
}
