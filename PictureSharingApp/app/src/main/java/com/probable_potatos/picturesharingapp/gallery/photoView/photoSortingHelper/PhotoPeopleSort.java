package com.probable_potatos.picturesharingapp.gallery.photoView.photoSortingHelper;

import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.ContentItem;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Item;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Photos;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.TitleItem;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.GroupResponseData;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.UserResponseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 09/12/2017.
 */

public class PhotoPeopleSort extends PhotoSort {


    GroupResponseData group_data;
    List<UserResponseData> user_list;


    GroupResponseData.GroupsImageResponseData[] image_list;
    List<Photos> nonpeople_list = new ArrayList<>();
    List<Photos> people_list = new ArrayList<>();

    public PhotoPeopleSort(GroupResponseData group_data, List<UserResponseData> user_list) {
        this.group_data = group_data;
        this.user_list = user_list;

        //TODO: warning should not be null at all, just in case
        if (group_data.images != null) {
            this.image_list = group_data.images;
        }
    }


    @Override
    public int getSortType() {
        return PhotoSort.SORT_PEOPLE;
    }

    @Override
    public List<Item> buildItemList() {
        List<Item> ret = new ArrayList<>();

        if (getSorted()) {

            ret.add(new TitleItem("People"));
            ret.add(new ContentItem(people_list));

            ret.add(new TitleItem("Non-People"));
            ret.add(new ContentItem(nonpeople_list));


        }
        return ret;
    }


    boolean getSorted() {

        List<GroupResponseData.GroupsImageResponseData>
                img_people_list = new ArrayList<>();

        List<GroupResponseData.GroupsImageResponseData>
                img_nonpeople_list = new ArrayList<>();

        for (int i = 0; i < image_list.length; i++) {

            if (image_list[i].is_people == 1) {
                img_people_list.add(image_list[i]);
            } else if (image_list[i].is_people == 0) {
                img_nonpeople_list.add(image_list[i]);
            }
        }

        for (GroupResponseData.GroupsImageResponseData each : img_people_list) {
            Photos photo = new Photos(each.image);
            people_list.add(photo);
        }

        for (GroupResponseData.GroupsImageResponseData each : img_nonpeople_list) {
            Photos photo = new Photos(each.image);
            nonpeople_list.add(photo);

        }


        return true;

    }


}
