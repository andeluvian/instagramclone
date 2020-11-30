package com.probable_potatos.picturesharingapp.gallery.albumView;

import com.probable_potatos.picturesharingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 29/11/2017.
 */

public class Folder {
    public String name;
    public int count;
    public int photoId;
    public String imgUrl = null;

    public Folder(String name, int count, int photoId) {
        this.name = name;
        this.count = count;
        this.photoId = photoId;
    }

    public Folder(String name, int count, String imgUrl) {
        this.name = name;
        this.count = count;
        this.imgUrl = imgUrl;
    }


    public static List<Folder> initializeData() {

        List<Folder> persons;
        persons = new ArrayList<>();
        persons.add(new Folder("Private", 5, R.drawable.album1));
        persons.add(new Folder("Public", 5, R.drawable.album1));
        persons.add(new Folder("Mook", 5, R.drawable.album1));
        persons.add(new Folder("Yoyoyoy", 5, R.drawable.album1));
        persons.add(new Folder("Hello", 5, R.drawable.album1));
        return persons;


    }

}
