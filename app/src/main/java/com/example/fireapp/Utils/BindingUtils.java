package com.example.fireapp.Utils;

import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.fireapp.R;

public class BindingUtils {
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, Uri imageUrl) {
        if (imageUrl != null) {

            Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
        } else {
            imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.profile_picture));
        }

    }
}
