package com.tentenacy.samplekakaomessageapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.talk.model.Friend
import com.kakao.sdk.user.UserApiClient
import com.tentenacy.samplekakaomessageapi.databinding.ActivityFriendsBinding

class FriendsActivity : AppCompatActivity() {
    val talkApiClient = TalkApiClient.instance
    lateinit var friendsAdapter: FriendsAdapter
    lateinit var friendsViewModel: FriendsViewModel
    private val onFriendItemClickListener = object : OnFriendItemClickListener {
        override fun onItemClick(friend: Friend) {
            val intent = Intent(this@FriendsActivity, MessageActivity::class.java)
            intent.putExtra("friend", friend)
            startActivity(intent)
        }
    }

    companion object {
        const val TAG = "FriendsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityFriendsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_friends)

        friendsAdapter = FriendsAdapter()
        friendsAdapter.setOnClickListener(onFriendItemClickListener = onFriendItemClickListener)
        binding.recyclerView.adapter = friendsAdapter
        friendsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FriendsViewModel::class.java)
        friendsViewModel.friends.observe(this, Observer {
            friendsAdapter.setItem(it)
        })

        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
            }
        }

        // 카카오톡 친구 목록 가져오기 (기본)
        talkApiClient.friends { friends, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡 친구 목록 가져오기 실패", error)
            } else if (friends != null) {
                Log.i(TAG, "카카오톡 친구 목록 가져오기 성공 \n${friends.elements.joinToString("\n")}")
                Log.e(TAG, "카카오톡 친구 목록 가져오기 ${friends.toString()}")
                // 친구의 UUID 로 메시지 보내기 가능
                friendsViewModel.setFriends(friends.elements)
            }
        }
    }
}