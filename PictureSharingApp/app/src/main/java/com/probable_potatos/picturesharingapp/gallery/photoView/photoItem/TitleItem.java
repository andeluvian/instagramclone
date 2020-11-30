package com.probable_potatos.picturesharingapp.gallery.photoView.photoItem;

/**
 * Created by dong on 06/12/2017.
 */

public class TitleItem extends Item {

    int position;

    String titleText;

    public TitleItem(String titleText) {
        this.titleText = titleText;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    @Override
    public int getItemType() {
        return Item.TITLE_ITEM_TYPE;
    }
}
