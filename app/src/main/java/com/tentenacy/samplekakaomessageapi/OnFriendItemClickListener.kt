package com.tentenacy.samplekakaomessageapi

import com.kakao.sdk.talk.model.Friend

interface OnFriendItemClickListener {
    fun onItemClick(friend: Friend)
}