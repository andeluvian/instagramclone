package com.probable_potatos.picturesharingapp.gallery.photoView.photoSortingHelper;

import android.content.Context;

import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Item;

import java.util.List;

/**
 * Created by dong on 09/12/2017.
 */

public abstract class PhotoSort {
    public static int SORT_PEOPLE = 1;
    public static int SORT_NAME = 0;

    public abstract int getSortType();
    public abstract List<Item> buildItemList();
}
