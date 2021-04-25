package com.tentenacy.samplekakaomessageapi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.talk.model.Friend

class FriendsViewModel : ViewModel() {
    private val mFriends = MutableLiveData<List<Friend>>()
    val friends: LiveData<List<Friend>> = mFriends

    fun setFriends(friends: List<Friend>) {
        mFriends.value = friends
    }
}