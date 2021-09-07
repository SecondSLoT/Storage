package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.RepositoryImpl
import com.secondslot.storage.data.repository.model.Character
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(private val repository: RepositoryImpl) {

    fun execute(sortField: String): Flow<List<Character>> {
        return repository.getCharacters(sortField)
    }
}