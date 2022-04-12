package com.example.fireapp;

import android.app.Application;
import android.telephony.BarringInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

public class GlobalViewModel extends AndroidViewModel {
    private MutableLiveData<String> UID = new MutableLiveData<>("");
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<List<User>> listOfUsers = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<String>> listOfUIDs = new MutableLiveData<>(new ArrayList<>());


    public GlobalViewModel(@NonNull Application application) {
        super(application);
    }

    public void setListOfUIDs(List<String> listOfUIDs) {
        this.listOfUIDs.setValue(listOfUIDs);
    }

    public MutableLiveData<List<String>> getListOfUIDs() {
        return listOfUIDs;
    }

    public void setUID(String UID) {
        this.UID.setValue(UID);
    }

    public MutableLiveData<String> getUID() {
        return UID;
    }

    public MutableLiveData<List<User>> getListOfUsers() {
        return listOfUsers;
    }

    public void addUser(User user) {
        getListOfUsers().getValue().add(user);
    }

    public void removeFollower(User user1) {
        List<User> arr = new ArrayList<>();
        for (User user2 : Objects.requireNonNull(getListOfUsers().getValue())) {

            if (!user1.getId().equals(user2.getId())) {
                arr.add(user2);
            }

        }
        setListOfUsers(arr);
        removeFollowersUID(user1);
    }

    private Boolean firstLog = true;

    public void setFirstLog(Boolean firstLog) {
        this.firstLog = firstLog;
    }

    public Boolean getFirstLog() {
        return firstLog;
    }

    public void removeFollowersUID(User user1) {
        List<String> uid = new ArrayList<>();
        for (String s : getListOfUIDs().getValue()) {
            if (!user1.getId().equals(s)) {
                uid.add(s);
            }
        }
        setListOfUIDs(uid);
    }

    public void setListOfUsers(List<User> listOfUsers) {
        this.listOfUsers.setValue(listOfUsers);
    }

    public void clearUserList() {
        setListOfUsers(new ArrayList<>());
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public MutableLiveData<User> getUser() {
        return user;
    }
}
