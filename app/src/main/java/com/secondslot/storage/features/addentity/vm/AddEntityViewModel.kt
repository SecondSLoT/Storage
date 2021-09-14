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

class AddEntityViewModel(val characterId: Int) : ViewModel() {

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

    fun onAddButtonClicked(character: Character) {
        viewModelScope.launch {
            insertCharacterUseCase.execute(character)
        }
        _characterAddedLiveData.value = true
    }

    fun onAddButtonComplete() {
        _characterAddedLiveData.value = false
    }

    fun onEditButtonClicked(character: Character) {
        viewModelScope.launch {
            updateCharacterUseCase.execute(character)
        }
        _characterAddedLiveData.value = true
    }
}
