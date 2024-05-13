package com.mkd.whatstheweather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mkd.whatstheweather.databinding.FavouriteItemBinding
import com.mkd.whatstheweather.room.Favourite

class FavouriteAdapter(private val onItemClicked: (Favourite) -> Unit) :
    ListAdapter<Favourite, FavouriteAdapter.FavouriteViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(FavouriteItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavouriteViewHolder(private val binding: FavouriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onItemClicked(getItem(adapterPosition)) }
        }

        fun bind(favourite: Favourite) {
            binding.favouriteCityName.text = favourite.city
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Favourite>() {
            override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite): Boolean =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite): Boolean =
                oldItem.city == newItem.city
        }
    }
}