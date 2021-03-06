package com.secondslot.storage.data.repository.mapper

import com.secondslot.storage.core.mapper.BaseMapper
import com.secondslot.storage.data.db.model.CharacterDb
import com.secondslot.storage.data.repository.model.Character

// It may be more practical to use an extension function for mapping.
// As we agreed, I won't fix it now, but take into account to do it in the next task
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
