package com.secondslot.storage.data.repository.mapper

import com.secondslot.storage.core.mapper.BaseMapper
import com.secondslot.storage.data.db.model.CharacterDb
import com.secondslot.storage.data.repository.model.Character

object LocalToItemMapper : BaseMapper<List<CharacterDb>, List<Character>> {

    override fun map(type: List<CharacterDb>?): List<Character> {
        return type?.map {
            Character(
                id = it.id,
                name = it.name,
                location = it.location,
                quote = it.quote
            )
        } ?: emptyList()
    }
}

object ItemToLocalMapper : BaseMapper<List<Character>, List<CharacterDb>> {

    override fun map(type: List<Character>?): List<CharacterDb> {
        return type?.map {
            CharacterDb(
                id = it.id,
                name = it.name,
                location = it.location,
                quote = it.quote
            )
        } ?: emptyList()
    }
}