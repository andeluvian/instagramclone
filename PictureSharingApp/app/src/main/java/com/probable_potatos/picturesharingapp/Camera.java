package com.probable_potatos.picturesharingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.probable_potatos.picturesharingapp.GroupManagement.JoinGroup;
import com.probable_potatos.picturesharingapp.Utility.Connect;
import com.probable_potatos.picturesharingapp.Utility.Utils;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Camera extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 1;
    private ProgressDialog progressDia;
    Uri imageURI;
    private StorageReference mStorage;
    FirebaseUser user;
    FirebaseDatabase database;
    String URLUser = Utils.URL + "users/update";
    String URLgrp = Utils.URL + "groups/upload_image";
    DatabaseReference groupRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if(!((Activity) this).isFinishing())
        {Toast.makeText(getApplicationContext(), "Camera Activity", Toast.LENGTH_SHORT).show();}

        mStorage = FirebaseStorage.getInstance().getReference();
        progressDia = new ProgressDialog(this);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        // Check that there is CAMERA activity
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // File creation
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error handling
            }
            // if we have a file to work with
            if (photoFile != null) {
                imageURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider", photoFile);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

            }
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            progressDia.setMessage("Uploading");
            progressDia.show();

            Uri uri = imageURI;
            StorageReference filepath = mStorage.child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDia.dismiss();
                    Uri image_URL = taskSnapshot.getDownloadUrl();
                    sendToAPI(image_URL.toString());

                    //send data to API



                    Intent intent = new Intent(Camera.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_CANCELED)
        {
            startActivity(new Intent(Camera.this,MainActivity.class));
            finish();
        }
    }


    private void sendToAPI(final String image_URL)
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        //final DatabaseReference userRef = database.getReference("users").child(user.getUid());
        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(user.getUid());

        final Connect connection = new Connect(getApplicationContext());
        final Connect connectiongrp = new Connect(getApplicationContext());


        final Map<String, String> parameters = new HashMap<>();
        final Map<String, String> parametersgrp = new HashMap<>();




        //GET groups!
        // Read from the database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();


                if(newPost.containsKey("groups"))
                {
                    Map<String, Object> groups = (Map<String, Object>) newPost;

                    String[] groupsFirstElement = StringToArray(groups.get("groups").toString());

                    String currentGroup = groupsFirstElement[0];

                    if(currentGroup == null || currentGroup.isEmpty())
                    {
                        // no group
                    }
                    else
                    {
                        final String grpURL = image_URL;
                        parametersgrp.put("grpName",currentGroup);
                        parametersgrp.put("images",grpURL);
                        parametersgrp.put("uid", user.getUid());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        parameters.put("images",image_URL);
        parameters.put("uid", user.getUid());

        user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful())
                        {
                            String idToken = task.getResult().getToken();
                            connection.volleyPostNoToast(URLUser, parameters,idToken);
                            connection.volleyPostNoToast(URLgrp, parametersgrp,idToken);

                        }
                        else
                        {
                            // Handle error -> task.getException();
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public String[] StringToArray(String array)
    {
        String[] items = array.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

        String[] results = new String[items.length];

        for (int i = 0; i < items.length; i++) {

            results[i] = items[i];

        }
        return results;
    }

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("imageURI", imageURI);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imageURI = savedInstanceState.getParcelable("imageURI");

    } */
}
