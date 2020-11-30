package com.probable_potatos.picturesharingapp.gallery.photoView.photoItem;

import com.probable_potatos.picturesharingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 29/11/2017.
 */

public class Photos {
    public String author;
    public String category;
    public int count;
    public int photoId;
    public String imgUrl;

    public Photos(String author, String category, int count, int photoId) {
        this.author = author;
        this.category = category;
        this.count = count;
        this.photoId = photoId;
    }

    public Photos(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public static List<Photos> initializeData() {

        List<Photos> persons;
        persons = new ArrayList<>();
        persons.add(new Photos("James","people", 5, R.drawable.album1));
        persons.add(new Photos("James","non-people", 5, R.drawable.album1));
        persons.add(new Photos("James","people", 5, R.drawable.album1));
        persons.add(new Photos("Daniel","non-people", 5, R.drawable.album1));
        persons.add(new Photos("Daniel","people", 5, R.drawable.album1));
        return persons;


    }

}
