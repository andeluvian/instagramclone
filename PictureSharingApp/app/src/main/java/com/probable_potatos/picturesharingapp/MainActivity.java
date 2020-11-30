package com.probable_potatos.picturesharingapp;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.probable_potatos.picturesharingapp.GroupManagement.CreateGroup;
import com.probable_potatos.picturesharingapp.GroupManagement.JoinGroup;
import com.probable_potatos.picturesharingapp.GroupManagement.ListGroups;

public class MainActivity extends AppCompatActivity {

    GridView mainGridView;

    private static final int joinGroupRequestCode = 1;

    static final String[] GridItems = new String[] {
            "Gallery",
            "Take Photo",
            "Create Group",
            "List Groups",
            "Join Group",
            "Settings"};

    public static int[] GridImages = {
            R.drawable.gallery,
            R.drawable.camera,
            R.drawable.creategroup,
            R.drawable.listgroups,
            R.drawable.joingroup,
            R.drawable.settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainGridView = (GridView) findViewById(R.id.MainGridView);
        mainGridView.setAdapter(new GridAdapter(this, GridItems, GridImages));

        mainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                switch(GridItems[position])
                {
                    case "Gallery" :
                        Intent gallery = new Intent(MainActivity.this,Gallery.class);
                        startActivity(gallery);
                        break;

                    case "Take Photo" :
                        Intent camera = new Intent(MainActivity.this,Camera.class);
                        startActivity(camera);
                        break;

                    case "Create Group" :
                        Intent createGroup = new Intent(MainActivity.this,CreateGroup.class);
                        startActivity(createGroup);
                        break;

                    case "List Groups" :
                        Intent listGroups = new Intent(MainActivity.this,ListGroups.class);
                        startActivity(listGroups);
                        break;

                    case "Join Group" :
                        Intent joinGroup = new Intent(MainActivity.this,JoinGroup.class);
                        startActivityForResult(joinGroup,joinGroupRequestCode);
                        break;

                    case "Settings" :
                        Intent settings = new Intent(MainActivity.this,Settings.class);
                        startActivity(settings);
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK ) {

            if(data.hasExtra("scannedQR"))
            {
                Toast.makeText(getApplicationContext(), data.getStringExtra("scannedQR").toString(), Toast.LENGTH_SHORT).show();
            }
        }


    }



}
