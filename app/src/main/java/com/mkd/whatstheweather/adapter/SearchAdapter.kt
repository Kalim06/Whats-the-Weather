package com.mkd.whatstheweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.databinding.SearchItemBinding
import com.mkd.whatstheweather.model.City

class SearchAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var data = listOf<City>()

    interface OnItemClickListener {
        fun onItemClick(city: City)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<City>) {
        data = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        data = emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(data[position])
                }
            }
        }

        fun bind(city: City) {
            with(binding) {
                searchItemText.text = root.context.getString(
                    R.string.search_item_name, city.name, city.sys?.country
                )
            }
        }
    }
}