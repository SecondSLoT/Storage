package com.secondslot.storage.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.secondslot.storage.StorageApplication
import com.secondslot.storage.data.db.dao.CharacterDao
import com.secondslot.storage.data.db.dao.CharacterDaoCursor
import com.secondslot.storage.data.repository.mapper.ItemToLocalMapper
import com.secondslot.storage.data.repository.mapper.LocalToItemMapper
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.di.ApplicationScope
import com.secondslot.storage.domain.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "RepositoryImpl"

@ApplicationScope
class RepositoryImpl : Repository {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var roomDao: CharacterDao

    @Inject
    lateinit var cursorDao: CharacterDaoCursor

    init {
        StorageApplication.getComponent().injectRepository(this)
    }

    private fun getDao(): CharacterDao {
        return when (prefs.getString("db_management_system", "1")) {
            "1" -> roomDao
            else -> cursorDao
        }
    }

    // Maybe better to make requests and mappings on IO?
    // Yes, of course! Room makes request in IO automatically, for cursor version I had
    // wrapped the request in a coroutine with IO thread, but didn't pay attention to that
    // the all path from DB to ViewModel is in main thread. I've changed the thread to IO in
    // StorageListViewModel, from where invocation starts. Thanks for remark!
    override fun getCharacters(sortField: String): Flow<List<Character>> {
        return getDao().getAllSorted(sortField).map { charactersDb ->
            Log.d(TAG, "getDao(), thread = " + Thread.currentThread().name)
            LocalToItemMapper.map(charactersDb)
        }
    }

    override suspend fun findCharacterById(id: Int): Character? {
        val characterDb = getDao().get(id)
        return characterDb?.let { LocalToItemMapper.map(listOf(characterDb))[0] }
    }

    override suspend fun insertCharacter(character: Character) {
        val characterDb = ItemToLocalMapper.map(listOf(character))[0]
        getDao().insert(characterDb)
    }

    override suspend fun insertCharacters(characters: List<Character>) {
        val charactersDb = ItemToLocalMapper.map(characters)
        getDao().insertAll(charactersDb)
    }

    override suspend fun updateCharacter(character: Character) {
        val characterDb = ItemToLocalMapper.map(listOf(character))[0]
        getDao().update(characterDb)
    }

    override suspend fun deleteCharacter(character: Character) {
        val characterDb = ItemToLocalMapper.map(listOf(character))[0]
        getDao().delete(characterDb)
    }

    override suspend fun clearDb() {
        getDao().clear()
    }
}
