package com.example.fireapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private BeginSignInRequest.Builder request;
    private SignInClient signInClient;
    private final String SERVER_URL = "670813705291-f9pg4qsvs8h94vj2sdmafh9pvja8vp9b.apps.googleusercontent.com";
    private final String FACEBOOK_SECRET = "4db9d1b1c92b49a80cb86484f75e7600";
    private NavController navController;
   private GlobalViewModel globalViewModel;

    //webId
    //670813705291-0oulm21q5ij7b5r8oiiubc8udouo39k1.apps.googleusercontent.com
    //Secret
    //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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        globalViewModel = new ViewModelProvider(this).get(GlobalViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(this);
        request = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(new BeginSignInRequest.GoogleIdTokenRequestOptions
                        .Builder().setSupported(true)
                        .setServerClientId(SERVER_URL)
                        .setFilterByAuthorizedAccounts(true)
                        .build());


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            User user = new User(mAuth.getUid(),currentUser.getDisplayName(),currentUser.getPhotoUrl());
            globalViewModel.setUser(user);
            navController.navigate(R.id.action_signinFragment_to_usersListFragment);
        }
    }


}