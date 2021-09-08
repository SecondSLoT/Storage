package com.secondslot.storage.domain.usecase

import com.secondslot.storage.data.repository.RepositoryImpl
import javax.inject.Inject

class NotifyDbChangedUseCase @Inject constructor(private val repository: RepositoryImpl) {

    fun execute(): Boolean = repository.notifyDbChanged()
}