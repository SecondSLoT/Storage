package com.secondslot.storage.features.addentity.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondslot.storage.StorageApplication
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.domain.usecase.FindCharacterByIdUseCase
import com.secondslot.storage.domain.usecase.InsertCharacterUseCase
import com.secondslot.storage.domain.usecase.UpdateCharacterUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEntityViewModel(private val characterId: Int) : ViewModel() {

    @Inject
    lateinit var insertCharacterUseCase: InsertCharacterUseCase

    @Inject
    lateinit var findCharacterByIdUseCase: FindCharacterByIdUseCase

    @Inject
    lateinit var updateCharacterUseCase: UpdateCharacterUseCase

    private var _characterAddedLiveData = MutableLiveData<Boolean>()
    val characterAddedLiveData get() = _characterAddedLiveData

    private var _getCharacterLiveData = MutableLiveData<Character>()
    val getCharacterLiveData get() = _getCharacterLiveData

    init {
        StorageApplication.getComponent().injectAddEntityViewModel(this)

        if (characterId != -1) {
            viewModelScope.launch {
                _getCharacterLiveData.value = findCharacterByIdUseCase.execute(characterId)
            }
        }
    }

    fun onAddButtonClicked(
        name: String,
        location: String,
        quote: String
    ) {

        if (characterId == -1) {
            val character = Character(
                name = name,
                location = location,
                quote = quote
            )
            insertCharacter(character)

        } else {
            val character = Character(
                id = characterId,
                name = name,
                location = location,
                quote = quote
            )
            editCharacter(character)
        }
    }

    private fun insertCharacter(character: Character) {
        viewModelScope.launch {
            insertCharacterUseCase.execute(character)
        }
        _characterAddedLiveData.value = true
    }

    fun onAddButtonComplete() {
        _characterAddedLiveData.value = false
    }

    private fun editCharacter(character: Character) {
        viewModelScope.launch {
            updateCharacterUseCase.execute(character)
        }
        _characterAddedLiveData.value = true
    }
}
