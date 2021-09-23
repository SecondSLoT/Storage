package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.RepositoryImpl
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.domain.Repository
import javax.inject.Inject

class InsertCharacterUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute(character: Character) {
        repository.insertCharacter(character)
    }
}
