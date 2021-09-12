package com.secondslot.storage.features.storagelist.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.storage.R
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.databinding.ItemCharacterBinding

class CharacterViewHolder(
    private val binding: ItemCharacterBinding,
    private val listener: CharacterListener
) : RecyclerView.ViewHolder(binding.root),
    PopupMenu.OnMenuItemClickListener {

    private var character: Character? = null

    fun bind(character: Character) {
        this.character = character

            binding.nameTextView.text = character.name

            // Check if location is empty or not and set appropriate text
            if (character.location.isNotEmpty()) {
                binding.locationTextView.text = character.location
            } else {
                binding.locationTextView.text = itemView.context.getString(R.string.unknown)
            }

            // Chek if quote is empty or not and set appropriate text
            if (character.quote.isNotEmpty()) {
                val quoteText = "\" ${character.quote}\""
                binding.quoteTextView.text = quoteText
            } else {
                binding.quoteTextView.text = itemView.context.getString(R.string.not_set)
            }

            binding.editButton.setOnClickListener {
                val popup = PopupMenu(itemView.context, it)
                popup.setOnMenuItemClickListener(this@CharacterViewHolder)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.character_menu, popup.menu)
                popup.show()
            }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_character -> character?.id?.let { listener.edit(it) }
            R.id.delete_character -> character?.let { listener.delete(it) }
        }
        return true
    }

    companion object {
        fun from(parent: ViewGroup, listener: CharacterListener): CharacterViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCharacterBinding.inflate(layoutInflater, parent, false)
            return CharacterViewHolder(binding, listener)
        }
    }
}