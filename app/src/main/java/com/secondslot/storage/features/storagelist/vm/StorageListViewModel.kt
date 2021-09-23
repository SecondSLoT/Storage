package com.secondslot.storage.features.storagelist.vm

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondslot.storage.StorageApplication
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.domain.fakedatafordb.FakeDataForDb
import com.secondslot.storage.domain.usecase.ClearDbUseCase
import com.secondslot.storage.domain.usecase.DeleteCharacterUseCase
import com.secondslot.storage.domain.usecase.GetCharactersUseCase
import com.secondslot.storage.domain.usecase.InsertCharacterUseCase
import com.secondslot.storage.domain.usecase.InsertCharactersUseCase
import com.secondslot.storage.util.ColumnNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class StorageListViewModel(private val prefs: SharedPreferences) : ViewModel() {

    private val fakeDataForDb = FakeDataForDb()
    private var sortField = getSortField()

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

    private var _charactersLiveData = MutableLiveData<List<Character>>()
    val charactersLiveData get() = _charactersLiveData

    private var _openAddEntityLiveData = MutableLiveData<Boolean>()
    val openAddEntityLiveData get() = _openAddEntityLiveData

    private val _clearDbSelectedLiveData = MutableLiveData<Boolean>()
    val clearDbSelectedLiveData get() = _clearDbSelectedLiveData

    private val _characterDeletedLiveData = MutableLiveData<Character?>()
    val characterDeletedLiveData get() = _characterDeletedLiveData

    init {
        StorageApplication.getComponent().injectStorageListViewModel(this)
    }

    private fun getCharacters() {
        viewModelScope.launch(Dispatchers.IO) {
            getCharactersUseCase.execute(sortField).collect {
                withContext(Dispatchers.Main) { _charactersLiveData.value = it }
            }
        }
    }

    fun onFabClicked() {
        _openAddEntityLiveData.value = true
    }

//    It isn't the best way to handle a single LiveData event. Look this:
//    https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
    // I used exact this approach from this article (I even have this link in my bookmarks!) in my
    // previous homework, but then I saw this method in google codelabs, so I didn't know what is
    // better to use. I will use the approach from article in my further projects, but won't fix
    // this code right now (because I know how to do it and don't want to spend my time on it now).
    // Maybe I will fix this later
    fun onFabClickedComplete() {
        _openAddEntityLiveData.value = false
    }

    private fun getSortField(): String =
        when (prefs.getString("sort_by", ColumnNames.ID.value)) {
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
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("myLogs", "Adding fake entities to DB")
            val list = fakeDataForDb.generateFakeData(NUMBER_ENTITIES_TO_GENERATE) // magic number
                                                                                   // Fixed
            insertCharactersUseCase.execute(list)
        }
    }

    fun onRestoreCharacter(character: Character) {
        viewModelScope.launch {
            insertCharacterUseCase.execute(character)
        }
    }

    fun onRestoreCharacterCompleted() {
        _characterDeletedLiveData.value = null
    }

    fun onDeleteButtonClicked(character: Character) {
        viewModelScope.launch {
            deleteCharacterUseCase.execute(character)
            _characterDeletedLiveData.value = character
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

    companion object {
        private const val NUMBER_ENTITIES_TO_GENERATE = 10
    }
}
