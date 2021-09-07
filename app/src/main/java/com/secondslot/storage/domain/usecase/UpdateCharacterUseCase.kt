package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.RepositoryImpl
import com.secondslot.storage.data.repository.model.Character
import javax.inject.Inject

class UpdateCharacterUseCase @Inject constructor(private val repository: RepositoryImpl) {

    suspend fun execute(character: Character) {
        repository.updateCharacter(character)
    }
}