package com.probable_potatos.picturesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.probable_potatos.picturesharingapp.gallery.RecyclerItemClickListener;
import com.probable_potatos.picturesharingapp.gallery.RequestServer;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.GroupResponseData;
import com.probable_potatos.picturesharingapp.gallery.albumView.Folder;
import com.probable_potatos.picturesharingapp.gallery.albumView.GalleryAlbumAdapter;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.UserResponseData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Gallery extends AppCompatActivity {

    RecyclerView recylcler_view_manager;
    RequestServer req;

    UserResponseData user_data;
    GroupResponseData group_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toast.makeText(getApplicationContext(), "Gallery Activity", Toast.LENGTH_SHORT).show();

        recylcler_view_manager = (RecyclerView) findViewById(R.id.gallery_recycler);

        recylcler_view_manager.setHasFixedSize(true);
        //Tutorial: https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465

        GridLayoutManager grid_manager = new GridLayoutManager(this, 2);


        //TODO: interface is here!
        Folder test_folder = new Folder("yo", 1, R.id.folderPic);
        List<Folder> myFolder = new ArrayList<Folder>();

        myFolder.add(new Folder("test", 2, "https://firebasestorage.googleapis.com/v0/b/potato-1aac5.appspot.com/o/test2.jpg?alt=media&token=555ac0bd-d3f3-4100-8e16-76aebd9b8e99"));
        myFolder.add(new Folder("lalal", 3, R.drawable.album1));

        GalleryAlbumAdapter adapter = new GalleryAlbumAdapter(this, myFolder);

        recylcler_view_manager.setLayoutManager(grid_manager);

        recylcler_view_manager.setAdapter(adapter);


        req = RequestServer.getInstance();
        req.setUserId(getCurrentUID());


        Runnable uiThread = new Runnable() {
            @Override
            public void run() {


                req.fetchCurrentUserData();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        //Code that updat UI

                        updateUI(req.getUserData(), req.getGroupData());


                    }
                });

            }
        };


        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(uiThread, 0, 10, TimeUnit.SECONDS);


    }


    private String getCurrentUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
//        return "FFX6wEOOaLdzIsQTXmXSGsz7pHC2";
    }


    void updateUI(UserResponseData userData, List<GroupResponseData> grpData) {


        GalleryAlbumAdapter adapter = new GalleryAlbumAdapter(this, creatFolder(userData, grpData));
        recylcler_view_manager.setAdapter(adapter);
        recylcler_view_manager.setContextClickable(true);
        recylcler_view_manager.addOnItemTouchListener(new RecyclerItemClickListener(this, recylcler_view_manager, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                System.out.println("" + position);

                Intent i = new Intent(Gallery.this, GalleryPhotoPage.class);
                i.putExtra("uid", getCurrentUID());
                i.putExtra("folder_position", position);

                startActivity(i);

            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));

    }

    List<Folder> creatFolder(UserResponseData data, List<GroupResponseData> grpData) {
        List<Folder> ret = new ArrayList<Folder>();

        //private folder
        if (data.images == null) {
            ret.add(new Folder("Private", 0, R.drawable.nothing));
        } else {
            ret.add(new Folder("Private", data.images.length, data.images[0]));

        }

        //group folder

        for (GroupResponseData grp : grpData) {


            if (grp.images == null || grp.images.length <= 0) {
                ret.add(new Folder(grp.grpName, 0, R.drawable.nothing));
            } else {
                ret.add(new Folder(grp.grpName, grp.images.length, grp.images[0].image));
            }

        }


        return ret;
    }


}
