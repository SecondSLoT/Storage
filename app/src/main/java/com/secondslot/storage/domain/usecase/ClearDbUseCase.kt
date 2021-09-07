package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.RepositoryImpl
import javax.inject.Inject

class ClearDbUseCase @Inject constructor(private val repository: RepositoryImpl) {

    suspend fun execute() {
        repository.clearDb()
    }
}