package com.probable_potatos.picturesharingapp.gallery.responseDataModel;

/**
 * Created by dong on 07/12/2017.
 */

public class GroupResponseData  extends ResponseData{



    public long createDate;
    public long lastModified;
    public String joinID;
    public String[] members;
    public GroupsImageResponseData[] images;
    public String owner;
    public String grpName;

    @Override
    public void dump() {
        System.out.println(joinID);
        System.out.println(owner);
        for (String e : members)
        {
            System.out.println(" m-> "+ e);
        }

    }


    public class GroupsImageResponseData {
        public String image;
        public int is_people;
        public String uid;
    }
}