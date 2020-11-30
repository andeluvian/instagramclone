package com.probable_potatos.picturesharingapp.GroupManagement;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.probable_potatos.picturesharingapp.CreateQRCode;
import com.probable_potatos.picturesharingapp.R;
import com.probable_potatos.picturesharingapp.Utility.Connect;
import com.probable_potatos.picturesharingapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ListGroups extends AppCompatActivity {


    TextView groupName;
    TextView groupDuration;
    ImageView qrImage;
    Button exitGroupButton;
    LinearLayout memberList;
    String currentGroup;
    FirebaseDatabase database;
    FirebaseUser user;
    String URL = Utils.URL + "groups/update";
    String URLLeave = Utils.URL + "users/removegroup";
    Map<String, Object> newPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        groupName = (TextView) findViewById(R.id.groupName);
        groupDuration = (TextView) findViewById(R.id.groupDuration);
        qrImage = (ImageView) findViewById(R.id.qrImage);
        exitGroupButton = (Button) findViewById(R.id.exitGroupButton);
        memberList = (LinearLayout) findViewById(R.id.memberList);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(user.getUid());

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

                    currentGroup = groupsFirstElement[0];

                    if(currentGroup == null || currentGroup.isEmpty())
                    {
                        ClearView();
                    }
                    else
                    {
                        getActiveGroup(currentGroup);
                    }
                }
                else
                {
                    ClearView();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void ClearView()
    {
        groupName.setText("No active group.");
        groupDuration.setText("");
        memberList.removeAllViews();
        qrImage.setImageResource(0);
    }

    public void getActiveGroup(String group) {

        DatabaseReference groupRef = database.getReference("groups").child(group);

        // Read from the database
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    newPost = (Map<String, Object>) dataSnapshot.getValue();

                    addGroupData(newPost);
                }catch (NullPointerException e)
                {
                    LeaveGroup();
                    ClearView();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void addGroupData(Map<String, Object> newPost){

        String expiration = newPost.get("expire").toString();
        String currentTime = String.valueOf(System.currentTimeMillis());
        String timeLeft = GetTimeLeft(currentTime,expiration);
        groupDuration.setText(timeLeft);

        String joinID = newPost.get("joinID").toString();
        groupName.setText(currentGroup);
        CreateQRCode newQR = new CreateQRCode(currentGroup+joinID);
        Bitmap bitmap = newQR.GenerateBitmap();
        qrImage.setImageBitmap(bitmap);



        if (newPost.containsKey("members"))
        {
            String[] members = StringToArray(newPost.get("members").toString());

            for(int i = 0; i < members.length; i++)
            {
                GetEmailFromUid(members[i]);
            }
        }
    }


    public void GetEmailFromUid(final String uid)
    {
        DatabaseReference usrRef = database.getReference("users").child(uid);

        // Read from the database
        usrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    newPost = (Map<String, Object>) dataSnapshot.getValue();

                    AddEmailToListView(newPost.get("email").toString());

                }catch (NullPointerException e)
                {
                    AddEmailToListView("Anonymous: " + uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void AddEmailToListView(String email)
    {
        TextView memberName = new TextView(this);
        memberName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        memberName.setText(email);
        memberList.addView(memberName);
    }

    public void ExitGroup(View view)
    {
        try
        {
            DatabaseReference grpRef = database.getReference("groups").child(currentGroup);

            // Read from the database
            grpRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try {

                        Map<String, Object> grpPost = (Map<String, Object>) dataSnapshot.getValue();

                        if(grpPost.get("owner").toString().equals(user.getUid()))
                        {
                            Toast.makeText(getApplicationContext(), "Deleting group..", Toast.LENGTH_SHORT).show();
                            DeleteGroup();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Leaving group..", Toast.LENGTH_SHORT).show();
                            LeaveGroup();

                        }

                    }catch (NullPointerException e)
                    {

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (NullPointerException e)
        {
            Toast.makeText(getApplicationContext(), "Wait for group to load or join/create a group.", Toast.LENGTH_SHORT).show();

        }


    }


    // removes the users/uid/groups
    public void LeaveGroup()
    {
        final Connect connection = new Connect(getApplicationContext());
        final Map<String, String> parameters = new HashMap<>();

        parameters.put("uid", user.getUid());

        // get idToken and use it in volleyPost
        user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            connection.volleyPostNoToast(URLLeave,parameters,idToken);

                        } else {
                            // Handle error -> task.getException();
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Delete group by setting expiration to current time
    public void DeleteGroup()
    {

        final Connect connection = new Connect(getApplicationContext());
        long timestampLong = System.currentTimeMillis();

        final Map<String, String> parameters = new HashMap<>();

        parameters.put("uid", user.getUid());
        parameters.put("grpName", currentGroup);
        parameters.put("expire", String.valueOf(timestampLong));

        // get idToken and use it in volleyPost
        user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            connection.volleyPostNoToast(URL,parameters,idToken);

                        } else {
                            // Handle error -> task.getException();
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        LeaveGroup();
        ClearView();
    }


    // Returns minutes
    public String GetTimeLeft(String start, String end)
    {
        long s = Long.parseLong(start);
        long e = Long.parseLong(end);
        long left = e - s;

        left = TimeUnit.MILLISECONDS.toMinutes(left);

        if (left > 0)
        {
            return String.valueOf(left)  + " minutes.";
        }
        else
        {
            return "Time expired.";
        }

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
