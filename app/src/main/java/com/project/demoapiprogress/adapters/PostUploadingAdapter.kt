package com.project.demoapiprogress.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.demoapiprogress.R
import com.project.demoapiprogress.databinding.ItemPostUploadingBinding
import com.project.demoapiprogress.upload.PostProgressItem
import com.project.demoapiprogress.upload.ProgressItem
import com.project.demoapiprogress.utils.MyUtils

class PostUploadingAdapter(
    private val context: Context,
    private val list: ArrayList<ProgressItem>,
    private val callback: Callback
) :
    RecyclerView.Adapter<PostUploadingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPostUploadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface Callback {
        fun onRetryUpload(position: Int, model: ProgressItem)
        fun onUploadOptions(position: Int, model: ProgressItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostUploadingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= list.size) return
        val binding = holder.binding
        var model = list[position] ?: return
        model = model as PostProgressItem

        if (model.error.isEmpty()) {
            binding.btnRetry.visibility = View.GONE
            binding.textUpload.visibility = View.VISIBLE
            binding.tvTitle.text = "Uploading Post"
            binding.textUpload.text = "${model.progress}%"
            val grey = ContextCompat.getColor(context, R.color.grey_lightest)
            binding.tvTitle.setTextColor(grey)
        } else {
            binding.btnRetry.visibility = View.VISIBLE
            binding.textUpload.visibility = View.GONE
            binding.tvTitle.text = "Failed:" + model.error
            val red = ContextCompat.getColor(context, R.color.red_delete)
            binding.tvTitle.setTextColor(red)
        }

        binding.progressBar.progress = model.progress

        if (model.mediaType == "image")
            MyUtils.loadImageOffline(context, model.media[0], binding.leftUploadIc)
        else
            binding.leftUploadIc.setImageResource(R.drawable.placeholder_image)

        binding.btnRetry.setOnClickListener { callback.onRetryUpload(position, model) }
        binding.ivMore.setOnClickListener { callback.onUploadOptions(position, model) }
    }

    fun update(position: Int) {
        notifyItemChanged(position)
    }

    fun notifyRemoved(position: Int) {
        if (list.isNotEmpty() && position < list.size)
            list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}