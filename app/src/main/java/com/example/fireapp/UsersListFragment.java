package com.example.fireapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.fireapp.databinding.FragmentUsersListBinding;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class UsersListFragment extends Fragment {

    private static final String TAG = "FacebookLogin";

    FragmentUsersListBinding binding;
    NavController navController;
    Button logoutButton;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private GlobalViewModel globalViewModel;
    private String accessToken;
    private FirebaseDatabase database;
    private LoginManager loginManager;
    private DatabaseReference dRef;
    private List<String> usersID = new ArrayList<>();
    private boolean getUserFollowers = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users_list, container, false);
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
        binding.setViewModel(globalViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        navController = Navigation.findNavController(requireActivity(), R.id.main_container);
        loginManager = LoginManager.getInstance();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        dRef = database.getReference("users");
        logoutButton = binding.logoutBtn;
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            globalViewModel.setUser(null);
            navController.navigate(R.id.action_usersListFragment_to_signinFragment);
            loginManager.logOut();
        });
        getUsers();
        globalViewModel.getListOfUIDs().observe(getViewLifecycleOwner(), strings -> {
            if (!globalViewModel.getFirstLog()) {
                dRef.child(mAuth.getUid()).child("followers").setValue(new Gson().toJson(strings));
                dRef.child(mAuth.getUid()).child("followers_count").setValue(strings.size());
                setAdapter();
            }
        });

        return binding.getRoot();
    }

    private void setAdapter() {
        Log.e(TAG, "Adapter");
        LinearLayoutManager linearLayout = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        binding.listOfUsers.setLayoutManager(linearLayout);
        binding.listOfUsers.setAdapter(new UsersAdapter(globalViewModel.getListOfUsers().getValue(), requireActivity()));
    }

    private void getUsers() {
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, snapshot.getKey());
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals(mAuth.getUid())) {
                        for (DataSnapshot childChild : child.getChildren()) {
                            if (childChild.getKey().equals("followers")) {
                                try {
                                    JSONArray str = new JSONArray(String.valueOf(childChild.getValue()));
                                    for (int i = 0; i < str.length(); i++) {
                                        Log.e(TAG, "e : " + str.getString(i));
                                        globalViewModel.getListOfUIDs().getValue().add(str.getString(i));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "e : " + e.getMessage());
                                    Log.e(TAG, "e : " + e.getCause());

                                }

                            }
                        }
                    }
                }
                getFollowers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public List<String> getUsersID() {
        return usersID;
    }

    private void getFollowers() {
        globalViewModel.clearUserList();
        dRef.getDatabase().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot childChild : child.getChildren()) {
                        Log.e(TAG, "CHi : " + childChild.getKey());
                        if (isFollower(childChild.getKey())) {

                            User follower = new User();
                            for (DataSnapshot childChildChild : childChild.getChildren()) {

                                switch (Objects.requireNonNull(childChildChild.getKey())) {
                                    case "id":
                                        follower.setId(childChildChild.getValue().toString());
                                        break;
                                    case "username":
                                        follower.setUsername(childChildChild.getValue().toString());
                                        break;
                                    case "profile_picture":

                                        follower.setProfilePicture(Uri.parse(childChildChild.getValue().toString()));
                                        break;
                                }


                            }
                            globalViewModel.addUser(follower);
                            Log.e(TAG, "Tag f : " + follower.getId());
                        }
                    }

                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter();
                        globalViewModel.setFirstLog(false);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isFollower(String uid) {
        List<String> value = globalViewModel.getListOfUIDs().getValue();

        for (int i = 0; i < value.size(); i++) {
            String s = value.get(i);
            if (s.equals(uid)) {
                return true;
            }

        }
        return false;
    }

}