package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.RepositoryImpl
import com.secondslot.storage.data.repository.model.Character
import javax.inject.Inject

class FindCharacterByIdUseCase @Inject constructor(private val repository: RepositoryImpl) {

    suspend fun execute(id: Int): Character? {
        return repository.findCharacterById(id)
    }
}
