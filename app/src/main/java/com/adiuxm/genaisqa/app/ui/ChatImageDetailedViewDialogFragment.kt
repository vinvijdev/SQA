package com.adiuxm.genaisqa.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import com.adiuxm.genaisqa.R
import com.bumptech.glide.Glide

class ChatImageDetailedView : DialogFragment() {

    private var url: String = ""
    fun newInstance(url: String): ChatImageDetailedView {
        val f = ChatImageDetailedView()
        val args = Bundle()
        args.putString("url", url)
        f.arguments = args
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = requireArguments().getString("url")!!;
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen
        );
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_fragment_chat_image, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide
            .with(requireContext())
            .load(url)
            .centerCrop()
            .into(view.findViewById<AppCompatImageView>(R.id.chat_image));
        view.findViewById<AppCompatImageView>(R.id.close_btn).setOnClickListener {
            dialog?.dismiss()
        }
    }
}