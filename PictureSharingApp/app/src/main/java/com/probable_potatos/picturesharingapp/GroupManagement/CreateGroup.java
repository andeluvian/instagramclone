package com.probable_potatos.picturesharingapp.GroupManagement;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.probable_potatos.picturesharingapp.R;
import com.probable_potatos.picturesharingapp.Utility.Connect;
import com.probable_potatos.picturesharingapp.Utility.Utils;


import java.util.HashMap;
import java.util.Map;


public class CreateGroup extends AppCompatActivity {

    EditText groupName;
    EditText groupDuration;
    ImageView barcodeImageView;
    String singleUseToken;
    String groupNameString;
    String groupDurationString;
    String currentGroup;
    String URL = Utils.URL + "groups/create";
    String URLUser = Utils.URL + "users/update";
    String URLJoin = Utils.URL + "groups/update";

    FirebaseUser user;
    boolean hasGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        hasGroup = true;

        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference userRef = database.getReference("users").child(user.getUid());

        // Read from the database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();

                if(newPost.containsKey("groups"))
                {
                    Map<String, Object> groups = (Map<String, Object>) newPost;

                    String[] groupsFirstElement = StringToArray(groups.get("groups").toString());

                    currentGroup = groupsFirstElement[0];

                    if(currentGroup == null || currentGroup.isEmpty())
                    {
                        // no groups, do nothing
                        hasGroup = false;
                    }
                    else
                    {
                        hasGroup = true;
                    }

                }
                else
                {
                    hasGroup = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //onClick button event function
    public void CreateGroup(View view)
    {


        groupName = findViewById(R.id.groupNameInput);
        groupDuration = findViewById(R.id.groupDurationInput);
        barcodeImageView = findViewById(R.id.qrImage);

        groupNameString = groupName.getText().toString();
        groupDurationString = groupDuration.getText().toString();

        if( !TextUtils.isDigitsOnly(groupDurationString))
        {
            Toast.makeText(getApplicationContext(), "Duration must be integer!", Toast.LENGTH_SHORT).show();
        }
        else if(groupNameString.length() == 0 && groupDurationString.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Enter a Groupname and Duration.", Toast.LENGTH_SHORT).show();
        }
        else if(!groupNameString.matches("[a-zA-Z]+"))
        {
            Toast.makeText(getApplicationContext(), "Please name the group alphabetically a-z and A-Z.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(hasGroup)
            {
                Toast.makeText(getApplicationContext(),"Leave group before creating a new one: " + currentGroup, Toast.LENGTH_SHORT).show();

            }
            else
            {
                AddGroupToCloud();
            }
        }

    }

    //TODO add group to cloud/firebase with api
    // return true if group was created successfully
    public void AddGroupToCloud()
    {
        final Connect connection = new Connect(getApplicationContext());
        final Connect connectionUser = new Connect(getApplicationContext());
        final Connect connectionJoin = new Connect(getApplicationContext());

        String expiration = GenerateExpirationDate(groupDurationString);
        GroupInfo info = new GroupInfo(groupNameString,user.getUid(),expiration);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put("uid", info.getUid());
        parameters.put("grpName", info.getGrpName());
        parameters.put("owner",info.getUid());
        parameters.put("expire", info.getExpire());

        final Map<String, String> userParameters = new HashMap<>();
        userParameters.put("uid",user.getUid());
        userParameters.put("groups",info.getGrpName());

        final Map<String, String> joinParameters = new HashMap<>();
        //joinParameters.put("uid",user.getUid());
        joinParameters.put("members",user.getUid());
        joinParameters.put("grpName",info.getGrpName());

        // get idToken and use it in volleyPost
        user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            connection.volleyPostNoToast(URL,parameters,idToken);
                            connectionUser.volleyPostNoToast(URLUser,userParameters,idToken);
                            connectionJoin.volleyPostNoToast(URLJoin,joinParameters,idToken);
                            Toast.makeText(getApplicationContext(), "group created! check 'List groups' for details.", Toast.LENGTH_SHORT).show();

                        } else {
                            // Handle error -> task.getException();
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public String GenerateExpirationDate(String duration)
    {
        int groupDuration = Integer.parseInt(duration);
        long timestampLong = System.currentTimeMillis();
        long durationInMS = groupDuration * 60 * 1000;
        long expirationTimestamp = timestampLong + durationInMS;
        String timestamp = String.valueOf(expirationTimestamp);

        return timestamp;
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

}
