package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.domain.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(private val repository: Repository) {

    fun execute(sortField: String): Flow<List<Character>> {
        return repository.getCharacters(sortField)
    }
}
