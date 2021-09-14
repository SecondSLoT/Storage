package com.secondslot.storage.features.storagelist.ui

import com.secondslot.storage.data.repository.model.Character

interface CharacterListener {

    fun edit(id: Int)

    fun delete(character: Character)
}
