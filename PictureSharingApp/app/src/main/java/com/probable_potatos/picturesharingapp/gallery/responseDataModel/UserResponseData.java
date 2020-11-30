package com.probable_potatos.picturesharingapp.gallery.responseDataModel;

/**
 * Created by dong on 07/12/2017.
 */

public class UserResponseData extends ResponseData{
    public String email;
    public String first_name;
    public String[] groups;
    public String[] images;
    public String last_name;
    public String uid;

    @Override
    public void dump() {
        System.out.println(email);
        System.out.println(first_name);
        System.out.println(last_name);
        System.out.println("groups{");

        if(groups != null) {
            for (String s : groups) {
                System.out.print("  " + s);
            }
            System.out.println("\n}");
        }

        if (images != null) {
            System.out.println("images{");
            for (String s : images)
            {
                System.out.print("  "+ s);
            }
            System.out.println("\n}");
        }


    }


}
