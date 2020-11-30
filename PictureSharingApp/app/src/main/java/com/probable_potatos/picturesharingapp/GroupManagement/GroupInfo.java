package com.probable_potatos.picturesharingapp.GroupManagement;

/**
 * Created by kaisti on 05/12/2017.
 */

public class GroupInfo {
    String grpName;
    String uid;
    String expire;

    public GroupInfo(String grpName, String uid, String expiration) {
        this.grpName = grpName;
        this.uid = uid;
        this.expire = expiration;
    }

    public String getGrpName() {
        return grpName;
    }

    public String getUid() {
        return uid;
    }

    public String getExpire() {
        return expire;
    }
}
