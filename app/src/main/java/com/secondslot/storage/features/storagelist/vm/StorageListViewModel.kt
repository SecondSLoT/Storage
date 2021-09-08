package com.secondslot.storage.features.storagelist.vm

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondslot.storage.StorageApplication
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.domain.FakeDataForDb.FakeDataForDb
import com.secondslot.storage.domain.usecase.*
import com.secondslot.storage.util.ColumnNames
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class StorageListViewModel(private val prefs: SharedPreferences) : ViewModel() {

    private val fakeDataForDb = FakeDataForDb()
    private var sortField = getSortField()
//    private var dbms = prefs.getString("db_management_system", "1")

    @Inject
    lateinit var getCharactersUseCase: GetCharactersUseCase

    @Inject
    lateinit var insertCharacterUseCase: InsertCharacterUseCase

    @Inject
    lateinit var insertCharactersUseCase: InsertCharactersUseCase

    @Inject
    lateinit var deleteCharacterUseCase: DeleteCharacterUseCase

    @Inject
    lateinit var clearDbUseCase: ClearDbUseCase

    @Inject
    lateinit var notifyDbChangedUseCase: NotifyDbChangedUseCase

    private var _charactersLiveData = MutableLiveData<List<Character>>()
    val charactersLiveData get() = _charactersLiveData

    private var _openAddEntityLiveData = MutableLiveData<Boolean>()
    val openAddEntityLiveData get() = _openAddEntityLiveData

    private val _clearDbSelectedLiveData = MutableLiveData<Boolean>()
    val clearDbSelectedLiveData get() = _clearDbSelectedLiveData

    init {
        StorageApplication.getComponent().injectStorageListViewModel(this)
        getCharacters()
    }

    private fun getCharacters() {
//        Log.d("myLogs", "sortField = $sortField, getCharacters()")
        viewModelScope.launch {
            getCharactersUseCase.execute(sortField).collect {
                _charactersLiveData.value = it
            }
        }
    }

    fun onFabClicked() {
        _openAddEntityLiveData.value = true
    }

    fun onFabClickedComplete() {
        _openAddEntityLiveData.value = false
    }

    private fun getSortField(): String =
        when (prefs.getString("sort_by", "id")!!) {
            "name" -> ColumnNames.NAME.value
            "location" -> ColumnNames.LOCATION.value
            "quote" -> ColumnNames.QUOTE.value
            else -> ColumnNames.ID.value
        }

    fun onPrefsUpdated() {
        val newSortField = getSortField()
        if (newSortField != sortField) {
            sortField = newSortField
        }
        getCharacters()
    }

    /**
     * Generates some entities and adds them to database
     */
    fun onAddFakeDataSelected() {
        viewModelScope.launch {
            Log.i("myLogs", "Adding fake entities to DB")
            val list = fakeDataForDb.generateFakeData(10)
            insertCharactersUseCase.execute(list)
        }
    }

    fun onDeleteButtonClicked(character: Character) {
        viewModelScope.launch {
            deleteCharacterUseCase.execute(character)
        }
    }

    fun onClearDbSelected() {
        _clearDbSelectedLiveData.value = true
    }

    fun onClearDbSelectedComplete() {
        _clearDbSelectedLiveData.value = false
    }

    fun onClearDbConfirmed() {
        viewModelScope.launch {
            clearDbUseCase.execute()
        }
    }
}