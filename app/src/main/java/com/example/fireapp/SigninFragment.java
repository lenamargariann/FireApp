package com.example.fireapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.fireapp.databinding.FragmentSigninBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.WebDialog;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SigninFragment extends Fragment {

    private static final String TAG = "FacebookLogin";

    private FragmentSigninBinding binding;
    private LoginButton facebookLoginButton;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private GlobalViewModel globalViewModel;
    private String accessToken;
    private FirebaseDatabase database;
    private DatabaseReference dRef;
    private List<String> usersID = new ArrayList<>();
    private NavController navController;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dRef = database.getReference("users");
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signin, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        facebookLoginButton = binding.facebookLoginBtn;
        navController = Navigation.findNavController(requireActivity(), R.id.main_container);
        getUsers();
        this.accessToken = requireActivity().getSharedPreferences("fire_app", Context.MODE_PRIVATE).getString("accessToken", "");
        Log.e(TAG, "OnCreate");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setPermissions("email", "public_profile");
        if (!this.accessToken.equals("")) {
            JSONObject jsonObject = new JSONObject();
            try {
                AccessToken token = AccessToken.createFromJSONObject$facebook_core_release(jsonObject.put("token", accessToken));
                handleFacebookAccessToken(token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());

                }

                @Override
                public void onCancel() {
                    Log.e(TAG, "facebook:onCancel");
                }

                @Override
                public void onError(@NonNull FacebookException e) {
                    Log.e(TAG, "facebook:onError - " + e.getMessage());
                    Log.e(TAG, "facebook:onError - " + e.getLocalizedMessage());

                    Log.e(TAG, "facebook:onError - " + e.getCause());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        User user1 = new User(getAccessToken(), user.getDisplayName(), user.getPhotoUrl());
                        globalViewModel.setUser(user1);
                        dRef.child(mAuth.getUid()).child("id").setValue(mAuth.getUid());
                        dRef.child(mAuth.getUid()).child("username").setValue(user.getDisplayName());
                        dRef.child(mAuth.getUid()).child("profile_picture").setValue(user.getPhotoUrl().toString());
                        dRef.child(mAuth.getUid()).child("followers").setValue(new Gson().toJson(getListOfFollowers(mAuth.getUid())));
                        dRef.child(mAuth.getUid()).child("followers_count").setValue(getListOfFollowers(mAuth.getUid()).size());
                        navController.navigate(R.id.action_signinFragment_to_usersListFragment);

                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public List<String> getUsersID() {
        return usersID;
    }

    public void setUsersID(List<String> usersID) {
        this.usersID = usersID;
    }

    private void getUsers() {
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, snapshot.getKey());
                for (DataSnapshot child : snapshot.getChildren()) {
                    usersID.add(child.getKey());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private List<String> getListOfFollowers(String uid) {
        List<String> tempList = new ArrayList<>();
        for (String s : getUsersID()) {
            if (!uid.equals(s)) {
                tempList.add(s);
            }
        }
        globalViewModel.setListOfUIDs(tempList);
        return tempList;
    }
}