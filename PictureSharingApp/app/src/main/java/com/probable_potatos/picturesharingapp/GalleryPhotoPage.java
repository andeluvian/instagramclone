package com.probable_potatos.picturesharingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.probable_potatos.picturesharingapp.gallery.RequestServer;
import com.probable_potatos.picturesharingapp.gallery.photoView.GalleryPhotoViewListAdapter;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.ContentItem;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Item;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.Photos;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoItem.TitleItem;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoSortingHelper.PhotoNameSort;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoSortingHelper.PhotoPeopleSort;
import com.probable_potatos.picturesharingapp.gallery.photoView.photoSortingHelper.PhotoSort;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.GroupResponseData;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.UserResponseData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GalleryPhotoPage extends AppCompatActivity {

    String userID;
    int folder_position;

    GroupResponseData cur_grp;
    List<UserResponseData> cur_user_list = new ArrayList<>();
    ListView listView;

    List<Item> people_sort_list;
    List<Item> name_sort_list;

    int sort_type = PhotoSort.SORT_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_gallery);
        setContentView(R.layout.activity_gallery_photo_page);


        //try the first solution
        Fresco.initialize(this);

        //TODO: may useless
        userID = getIntent().getStringExtra("uid");
        folder_position = getIntent().getIntExtra("folder_position", 0);

        Toast.makeText(getApplicationContext(), "Gallery Photo, uid: " + userID + " pos: " + folder_position, Toast.LENGTH_SHORT).show();

        listView = (ListView) findViewById(R.id.photoListView);



        ArrayAdapter myAdapter = new GalleryPhotoViewListAdapter(this, fakeItemList());


        Runnable uiThread = new Runnable() {
            @Override
            public void run() {



                    final List<Item> updateList;


                    if (folder_position <= 0) {
                        updateList = new ArrayList<>();
                        updateList.add(new ContentItem(urlArray2PhotoList(RequestServer.getInstance().getUserData().images)));
//                    updateList = fakeItemList();


                    } else {

                        //The first one is private folder


                        cur_grp = RequestServer.getInstance().getGroupData().get(folder_position - 1);

                        cur_grp = RequestServer.getInstance().updateGroupData(cur_grp.grpName);



                        cur_user_list = RequestServer.getInstance().fetchUsersFromGroup(cur_grp);


                        name_sort_list = new PhotoNameSort(cur_grp, cur_user_list).buildItemList();
                        people_sort_list = new PhotoPeopleSort(cur_grp, cur_user_list).buildItemList();


                        updateList = (sort_type == PhotoSort.SORT_NAME) ? name_sort_list : people_sort_list;
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI(updateList);

                        }
                    });

            }
        };



        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(uiThread, 0, 10, TimeUnit.SECONDS);


    }

    private List<Photos> urlArray2PhotoList(String[] images) {
        List<Photos> ret = new ArrayList<>();
        for (String s : images) {
            ret.add(new Photos(s));
        }
        return ret;
    }

    void updateUI(List<Item> list) {

        System.out.println("updating GalleryPhotoPage UI. List size: " + list.size());
        ArrayAdapter myAdapter = new GalleryPhotoViewListAdapter(this, list);
        listView.setAdapter(myAdapter);


    }

    List<Item> fakeItemList() {

        List<Item> ret = new ArrayList<Item>();

        ret.add(new TitleItem("yoyoyo"));
        ret.add(new ContentItem(Photos.initializeData()));
        ret.add(new TitleItem("lalala"));
        ret.add(new ContentItem(Photos.initializeData()));
        ret.add(new TitleItem("hehehe"));
        ret.add(new ContentItem(Photos.initializeData()));

        return ret;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (folder_position == 0) {
            return false;
        }

            System.out.println("Created Menu!! For Sorting!");
            //no menu in private
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.peopleSortItem:
                // search action
                sort_type = PhotoSort.SORT_PEOPLE;
                updateUI(people_sort_list);
                return true;
            case R.id.nameSortItem:
                // location found
                sort_type = PhotoSort.SORT_NAME;
                updateUI(name_sort_list);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
