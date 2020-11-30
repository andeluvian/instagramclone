package com.probable_potatos.picturesharingapp.GroupManagement;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.probable_potatos.picturesharingapp.MainActivity;
import com.probable_potatos.picturesharingapp.R;
import com.probable_potatos.picturesharingapp.Utility.Connect;
import com.probable_potatos.picturesharingapp.Utility.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JoinGroup extends AppCompatActivity {

    SurfaceView cameraView;
    BarcodeDetector qrDetector;
    CameraSource cameraSource;
    TextView result;
    String resultText;
    String currentGroup;
    final int PermissionID = 1001;
    String groupnameFromQR;
    String joinIDFromQR;
    String URL = Utils.URL + "groups/update";
    String URLUser = Utils.URL + "users/update";
    FirebaseDatabase database;
    boolean hasGroup;
    FirebaseUser user;
    DatabaseReference groupRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        hasGroup = true;

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
        ActivateScanning();

    }

    public void ActivateScanning()
    {
        cameraView = (SurfaceView) findViewById(R.id.cameraPreview);

        qrDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(this, qrDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        result = (TextView) findViewById(R.id.result);

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission for camera
                    ActivityCompat.requestPermissions(JoinGroup.this,
                            new String[]{Manifest.permission.CAMERA},PermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        qrDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();

                if(qrcodes.size() != 0)
                {
                    qrDetector.release();


                    Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    qrcodes.valueAt(0);
                    resultText = qrcodes.valueAt(0).rawValue.toString();
                    JoinGroupWithQR();
                }
            }
        });
    }

    //TODO join a group API
    public void JoinGroupWithQR()
    {
        if(hasGroup)
        {
            Intent mainActivity = new Intent(JoinGroup.this,MainActivity.class);
            mainActivity.putExtra("scannedQR", "Error scanning QR. QR-qode must be valid and you can't be active in a group.");
            setResult(Activity.RESULT_OK, mainActivity);
            finish();
        }
        else
        {

            if(resultText.length() < 6)
            {
                groupnameFromQR = "";
            }
            else
            {
                joinIDFromQR = GetJoinIDFromQR(resultText);
                groupnameFromQR = GetGroupnameFromQR(resultText);
            }


            groupRef = database.getReference("groups").child(groupnameFromQR);

            // Read from the database
            groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();

                    try {
                        if(newPost.containsKey("joinID")) {

                            String joinIDFromFB = newPost.get("joinID").toString();

                            if(joinIDFromFB.equals(joinIDFromQR))
                            {
                                ConnectVolley();
                            }
                            else
                            {
                                Intent mainActivity = new Intent(JoinGroup.this,MainActivity.class);
                                mainActivity.putExtra("scannedQR", "not a group QR-code!");
                                setResult(Activity.RESULT_OK, mainActivity);
                                finish();
                            }


                        }
                    }catch (NullPointerException e)
                    {
                        Intent mainActivity = new Intent(JoinGroup.this,MainActivity.class);
                        mainActivity.putExtra("scannedQR", "not a group QR-code!");
                        setResult(Activity.RESULT_OK, mainActivity);
                        finish();
                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getApplicationContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    public void ConnectVolley()
    {
        final Connect connection = new Connect(getApplicationContext());
        final Connect connectionUser = new Connect(getApplicationContext());

        final Map<String, String> joinParameters = new HashMap<>();
        joinParameters.put("uid",user.getUid());
        joinParameters.put("grpName",groupnameFromQR);
        joinParameters.put("members",user.getUid());

        final Map<String, String> userParameters = new HashMap<>();
        userParameters.put("uid",user.getUid());
        userParameters.put("groups", groupnameFromQR);

        // get idToken and use it in volleyPost
        user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            connection.volleyPostNoToast(URL,joinParameters, idToken);
                            connectionUser.volleyPostNoToast(URLUser,userParameters, idToken);

                            Intent mainActivity = new Intent(JoinGroup.this,MainActivity.class);
                            mainActivity.putExtra("scannedQR", "QR-code scanned! check 'List groups' for details.");
                            setResult(Activity.RESULT_OK, mainActivity);
                            finish();

                        } else {
                            // Handle error -> task.getException();
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // remove joinid from string e.g. groupnamejoinID -> groupname
    public String GetGroupnameFromQR(String qrString)
    {
        String groupName;

        groupName = qrString.substring(0, qrString.length() - 5);

        return groupName;
    }
    // remove joinid from string e.g. groupnamejoinID -> joinID
    public String GetJoinIDFromQR(String qrString)
    {
        String joinID;

        joinID = qrString.substring(qrString.length() - 5);

        return joinID;
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
