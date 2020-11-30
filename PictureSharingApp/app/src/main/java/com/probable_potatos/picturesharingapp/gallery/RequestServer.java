package com.probable_potatos.picturesharingapp.gallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.probable_potatos.picturesharingapp.Utility.Utils;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.GroupResponseData;
import com.probable_potatos.picturesharingapp.gallery.responseDataModel.UserResponseData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by dong on 07/12/2017.
 */

public class RequestServer {


    private static final RequestServer instance = new RequestServer();

    private RequestServer() {
    }

    public static RequestServer getInstance() {
        return instance;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String serverAddr = Utils.URL;

    String serverResponse = "";

    OkHttpClient client = new OkHttpClient();


    short sync_lock = 0;

    public static String groupQueryAddr = serverAddr + "groups/getgroup/";
    public static String userQueryAddr = serverAddr + "users/get/";


    Context context;

    String userId = "";

    UserResponseData userData;
    List<GroupResponseData> groupData = new ArrayList<>();

    FirebaseUser firebase_user = null;


    public UserResponseData getUserData() {
        return userData;
    }

    public List<GroupResponseData> getGroupData() {
        return groupData;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public FirebaseUser getFirebase_user() {
        return firebase_user;
    }

    public void setFirebase_user(FirebaseUser firebase_user) {
        this.firebase_user = firebase_user;
    }

    String SyncDataWithToken(String url, final String jsonData)
    {
        String ret = "";
        final String in_url = url;
        // get idToken and use it in volleyPost
        firebase_user.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            System.out.println("idToken= "+ idToken);

                            SyncGetDataFromURL(in_url, jsonData, idToken);

                        } else {
                            // Handle error -> task.getException();
                            System.out.println("Error with token");
                        }
                    }
                });
        return ret;
    }

    public void fetchCurrentUserData() {

        userData = null;
        groupData = new ArrayList<>();

        userData = updateUserData(userId);

        if (userData.groups != null) {

            for (int i = 0; i < userData.groups.length; i++) {

                GroupResponseData grp = updateGroupData(userData.groups[i]);
                groupData.add(grp);
            }
        }
    }

    public List<UserResponseData> fetchUsersFromGroup(GroupResponseData grp) {
        List<UserResponseData> ret = new ArrayList<>();

        if (grp.members != null) {
            for (String s :
                    grp.members) {
                ret.add(updateUserData(s));
            }
        }
        return ret;
    }

    public UserResponseData updateUserData(String uid) {

        UserResponseData ret;

        Gson gson = new Gson();

        System.out.println("====> getting user "+uid+" data");


        String user_s = SyncGetDataFromURL(userQueryAddr, "{ \"uid\":\"" + uid + "\"}","fake");
        ret = gson.fromJson(user_s, UserResponseData.class);
        ret.uid = uid;

        ret.dump();
        System.out.println(" user data ends <=====");

        return ret;
    }

    public GroupResponseData updateGroupData(String grpName) {

        System.out.println("====>getting group "+grpName+" data from: " + grpName);


        String s = SyncGetDataFromURL(groupQueryAddr, "{ \"grpName\":\"" + grpName + "\"}","fake");

        Gson gson = new Gson();
        GroupResponseData ret = gson.fromJson(s, GroupResponseData.class);
        ret.grpName = grpName;
        ret.dump();
        System.out.println("group data <=====");


        return ret;

    }

    public String SyncGetDataFromURL(String url, String jsonData, String idToken) {
        String ret = "";

        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("idtoken",idToken)
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!response.isSuccessful()) {
            System.out.println("NOT SUCCESS CONNECTION");
        }

        try {
            ret = response.body().string();


            System.out.println("::: "+ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    //descrepted
    public void getDataFromURL(String url, String jsonData) {

        Response response;
        String ret = "";
        Call call;
        System.out.println("url:" + url);
        System.out.println("JSON: " + jsonData);
        System.out.println("synclock: " + sync_lock);


        try {
            RequestBody body = RequestBody.create(JSON, jsonData);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    sync_lock--;
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    if (response.isSuccessful()) {
                        serverResponse = responseBody.string();
                    }
                    sync_lock--;
                    call.cancel();
                }
            });
            call.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


    }


}
