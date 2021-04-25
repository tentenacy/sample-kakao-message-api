package com.tentenacy.samplekakaomessageapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.talk.model.Friend
import com.tentenacy.samplekakaomessageapi.databinding.RowFriendKakaoBinding

class FriendsAdapter: RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {
    var friends: List<Friend> = emptyList()
    lateinit var onFriendItemClickListener: OnFriendItemClickListener
    inner class FriendViewHolder(private val binding: RowFriendKakaoBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { view ->
                binding.friend?.let { friend ->
                    onFriendItemClickListener.onItemClick(friend)
                }
            }
        }
        fun bind(friend: Friend) {
            binding.friend = friend
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = RowFriendKakaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    fun setItem(friends: List<Friend>) {
        this.friends = friends
        notifyDataSetChanged()
    }

    fun setOnClickListener(
        onFriendItemClickListener: OnFriendItemClickListener
    ) {
        this.onFriendItemClickListener = onFriendItemClickListener
    }
}