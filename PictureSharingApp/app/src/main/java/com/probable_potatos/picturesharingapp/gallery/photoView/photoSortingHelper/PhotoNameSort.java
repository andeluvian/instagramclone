package com.probable_potatos.picturesharingapp.gallery.photoView.photoSortingHelper;

import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.ContentItem;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Item;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Photos;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.TitleItem;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.GroupResponseData;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.UserResponseData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dong on 10/12/2017.
 */

public class PhotoNameSort extends PhotoSort {


    GroupResponseData group_data;
    List<UserResponseData> user_list;

    /*
    * UID -> [Image_name, Image_name, .....]
    * .....
    *
    * */
    HashMap<String, List<String>> sort_table = new HashMap<>();

    List<String> name_list = new ArrayList<>();


    public PhotoNameSort(GroupResponseData group_data, List<UserResponseData> user_list) {
        this.group_data = group_data;
        this.user_list = user_list;
    }

    @Override
    public int getSortType() {
        return PhotoSort.SORT_NAME;
    }

    @Override
    public List<Item> buildItemList() {
        List<Item> ret = new ArrayList<>();


        sortData();


        for (Map.Entry<String, List<String>> entry :
                sort_table.entrySet()) {
            String name = findName(entry.getKey());
            List<String> urls = entry.getValue();

            ret.add(new TitleItem(name));

            ret.add(new ContentItem(toPhotoList(urls)));
        }


        return ret;
    }

    public static List<Photos> toPhotoList(List<String> urls) {
        List<Photos> ret = new ArrayList<>();
        for (String u : urls) {

            ret.add(new Photos(u));

        }
        return ret;
    }


    void sortData() {


        if (group_data.images == null) {
            return;
        }


        for (GroupResponseData.GroupsImageResponseData image :
                group_data.images) {

            if (sort_table.containsKey(image.uid)) {

                List<String> img_urls = sort_table.get(image.uid);
                img_urls.add(image.image);
            } else {
                //init
                List<String> empty = new ArrayList<>();
                empty.add(image.image);

                sort_table.put(image.uid, empty);
            }

        }

    }


    String findName(String uid) {


        String ret = uid;

        for (UserResponseData user :
                user_list) {

            if(user.uid.equals(uid)) {
                if (user.first_name == null || user.first_name.equals("")) {
                    ret = user.email;
                } else {
                    ret = user.first_name;
                }
            }
        }

        return ret;
    }


}
