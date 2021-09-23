package com.secondslot.storage.domain.usecase

import com.secondslot.storage.domain.Repository
import javax.inject.Inject

// You can inject the repository as an interface in all use cases.
// Yes, this it how it should be! I didn't figure out how to do this at the start and
// used this as a temporary edition. I was going to fix this, but missed it. My bad. Thanks!
class ClearDbUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute() {
        repository.clearDb()
    }
}
