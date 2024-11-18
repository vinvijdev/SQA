package com.adiuxm.genaisqa.app.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adiuxm.genaisqa.app.Constants
import com.adiuxm.genaisqa.data.remote.DataConstants
import com.adiuxm.genaisqa.data.remote.RecyclerViewDataModel
import com.adiuxm.genaisqa.databinding.ChatItemJouleBinding
import com.adiuxm.genaisqa.databinding.ChatItemUserBinding

class ChatAdapter(var ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: MutableList<RecyclerViewDataModel> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> return ViewHolderUser(
                ChatItemUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
            )

            2 -> return ViewHolderJoule(
                ChatItemJouleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
            )
        }
        return ViewHolderUser(
            ChatItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        lateinit var viewHolderUser: ViewHolderUser
        lateinit var viewHolderJoule: ViewHolderJoule
        when (holder.itemViewType) {
            1 -> {
                viewHolderUser = holder as ViewHolderUser
                if (data[position].chatType == Constants.CHAT_TYPE_TEXT) {
                    viewHolderUser.tvChatMainUser.visibility = View.VISIBLE
                    viewHolderUser.tvChatMainUserImage.visibility = View.GONE
                    viewHolderUser.tvChatMainUser.text = data[position].message
                } else if (data[position].chatType == Constants.CHAT_TYPE_IMAGE) {
                    viewHolderUser.tvChatMainUser.visibility = View.GONE
                    viewHolderUser.tvChatMainUserImage.visibility = View.VISIBLE
                    viewHolderUser.tvChatMainUserImage.setImageBitmap(
                        BitmapFactory.decodeFile(data[position].file?.absolutePath)
                    )
                }
            }

            2 -> {
                viewHolderJoule = holder as ViewHolderJoule
                viewHolderJoule.tvChatJoule.text = data[position].message
            }
        }
    }

    inner class ViewHolderUser(binding: ChatItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layoutMainUser = binding.layoutUser
        val tvChatMainUser = binding.tvChatMainUser
        val tvChatMainUserImage = binding.tvChatMainUserImage
    }

    inner class ViewHolderJoule(binding: ChatItemJouleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layoutJoule = binding.layoutJoule
        val tvChatJoule = binding.tvChatJoule
    }

    override fun getItemViewType(position: Int): Int {
        if (data[position].type == DataConstants.USER_TYPE_SENDER) {
            return 1
        } else if (data[position].type == DataConstants.USER_TYPE_JOULE) {
            return 2
        } else if (false) {

        }
        return 1
    }

    fun addItems(it: RecyclerViewDataModel) {
        data.add(it)
    }
}
