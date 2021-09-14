package com.secondslot.storage.domain

import com.secondslot.storage.data.repository.model.Character
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getCharacters(sortField: String): Flow<List<Character>>

    suspend fun findCharacterById(id: Int): Character?

    suspend fun insertCharacter(character: Character)

    suspend fun insertCharacters(characters: List<Character>)

    suspend fun updateCharacter(character: Character)

    suspend  fun deleteCharacter(character: Character)

    suspend fun clearDb()
}
