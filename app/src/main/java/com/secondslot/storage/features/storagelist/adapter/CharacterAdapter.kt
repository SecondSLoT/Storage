package com.secondslot.storage.features.storagelist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.features.storagelist.ui.CharacterListener
import com.secondslot.storage.features.storagelist.ui.CharacterViewHolder

class CharacterAdapter(
    private val listener: CharacterListener
) : ListAdapter<Character, CharacterViewHolder>(CharacterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {

    override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem == newItem
    }
}