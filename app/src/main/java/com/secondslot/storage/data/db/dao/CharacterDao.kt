package com.secondslot.storage.data.db.dao

import androidx.room.*
import com.secondslot.storage.data.db.model.CharacterDb
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacterDao {

    @Query(
        "SELECT * FROM characters ORDER BY " +
                "CASE WHEN :columnName = 'name' THEN name " +
                "WHEN :columnName = 'location' THEN location " +
                "WHEN :columnName = 'quote' THEN quote END COLLATE NOCASE"
    )
    abstract fun getAllSorted(columnName: String): Flow<List<CharacterDb>>

    @Query("SELECT * FROM characters WHERE id = :id")
    abstract suspend fun get(id: Int): CharacterDb?

    @Insert
    abstract suspend fun insert(characterDb: CharacterDb)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAll(charactersDb: List<CharacterDb>)

    @Update
    abstract suspend fun update(characterDb: CharacterDb)

    @Delete
    abstract suspend fun delete(characterDb: CharacterDb)

    @Query("DELETE FROM characters")
    abstract suspend fun clear()
}