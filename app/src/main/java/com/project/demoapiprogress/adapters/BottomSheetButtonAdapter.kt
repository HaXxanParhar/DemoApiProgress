package com.project.demoapiprogress.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.demoapiprogress.R
import com.project.demoapiprogress.databinding.ItemBottomSheetButtonBinding

class BottomSheetButtonAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
    private val callback: Callback
) :
    RecyclerView.Adapter<BottomSheetButtonAdapter.ViewHolder>() {
    private val defaultColor: Int = R.color.white
    private var size: Int = list.size
    private var textColors: ArrayList<Int> = ArrayList()

    init {
        for (i in 0 until size)
            textColors.add(defaultColor)
    }


    inner class ViewHolder(val binding: ItemBottomSheetButtonBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface Callback {
        fun onItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBottomSheetButtonBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return size
    }

    // -------------------------------------   B I N D I N G ---------------------------------------


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.line.visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.binding.tvOption.text = list[position]
        holder.binding.tvOption.setTextColor(ContextCompat.getColor(context, textColors[position]))

        holder.binding.root.setOnClickListener { callback.onItemClicked(position) }
    }

    fun updateColor(position: Int, color: Int) {
        if (position >= size) return
        textColors[position] = color
    }
}