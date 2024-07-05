package com.project.demoapiprogress.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.project.demoapiprogress.R
import com.project.demoapiprogress.databinding.ItemPagerImageBinding
import com.project.demoapiprogress.utils.MyUtils

class ImagePagerAdapter(
    val context: Context,
    var list: ArrayList<String?>,
    val offlineImages: Boolean,
    val isCloseable: Boolean = false,
    val callback: Callback
) : PagerAdapter(
) {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemPagerImageBinding.inflate(LayoutInflater.from(context), container, false)
        container.addView(binding.root)

        binding.ivMore.visibility = if (isCloseable) View.VISIBLE else View.GONE

        val media = list[position]
        if (media.isNullOrEmpty()) {
            binding.ivImage.setImageResource(R.drawable.placeholder_image)
        } else {
            if (offlineImages)
                MyUtils.loadImageOffline(context, media, binding.ivImage)
            else
                MyUtils.loadImage(context, media, binding.ivImage)

            binding.ivImage.setOnClickListener {
                callback.onPagerImageClicked(
                    position,
                    media,
                    offlineImages
                )
            }

            binding.ivMore.setOnClickListener { callback.onClose(position) }
        }

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj as View
    }

    fun updateList(media: ArrayList<String?>) {
        list = media
        notifyDataSetChanged()
    }

    interface Callback {
        fun onClose(position: Int)
        fun onPagerImageClicked(position: Int, image: String, isOfflineImage: Boolean)
    }
}
