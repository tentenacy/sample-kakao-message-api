package com.tentenacy.samplekakaomessageapi

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("thumbnailImage")
fun bindThumbnailImage(imageView: ImageView, url: String?) {
    if(!url.isNullOrBlank()) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load("https://firebasestorage.googleapis.com/v0/b/dailyshare-d074b.appspot.com/o/images%2Fuser.png?alt=media&token=c7db3b88-fb4a-4bdf-939d-7e45b0da4d8c")
            .into(imageView)
    }
}