package com.example.fireapp;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.Exclude;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    @SerializedName("id")
    private String id;
    @SerializedName("username")
    private String username = "";
    @SerializedName("followers_count")
    private int followersCount = 0;
    @SerializedName("profile_picture")
    private Uri profilePicture = null;
    @SerializedName("followers")
    private List<String> followers = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public User(String id, String username, Uri pic) {
        this.id = id;
        this.username = username;
        this.profilePicture = pic;
    }
    public User(){
        this.id = "";
        this.profilePicture=null;
        this.followers= new ArrayList<>();
        this.followersCount = 0;
        this.username= "";
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", followersCount=" + followersCount +
                ", profilePicture=" + profilePicture.toString() +
                ", followers=" + new Gson().toJson(followers) +
                '}';
    }

    public void setProfilePicture(Uri profilePicture) {

        this.profilePicture = profilePicture;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public List<String> getFollowers() {
        return followers;
    }


    public int getFollowersCount() {
        return followersCount;
    }


    public String getUsername() {
        return username;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
