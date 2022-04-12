package com.example.fireapp;

import android.app.Activity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fireapp.databinding.ListItemBinding;
import com.facebook.appevents.suggestedevents.ViewOnClickListener;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private List<User> userList;
    private ListItemBinding binding;
    private GlobalViewModel globalViewModel;
    private FragmentActivity activity;

    public UsersAdapter(List<User> userList, FragmentActivity activity) {
        this.userList = userList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.list_item, parent, false);
        globalViewModel = new ViewModelProvider(activity).get(GlobalViewModel.class);
        return new UserViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bindUser(position);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public UserViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
        }

        private void bindUser(int pos) {
            binding.setUser(userList.get(pos));
            binding.setViewModel(globalViewModel);
        }
    }
}
