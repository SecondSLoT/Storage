package com.secondslot.storage.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.secondslot.storage.data.db.CharacterTable
import com.secondslot.storage.data.db.model.CharacterDb
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query(
        "SELECT * FROM ${CharacterTable.TABLE_NAME} ORDER BY " +
                "CASE WHEN :columnName = '${CharacterTable.NAME}' THEN name " +
                "WHEN :columnName = '${CharacterTable.LOCATION}' THEN location " +
                "WHEN :columnName = '${CharacterTable.QUOTE}' THEN quote END COLLATE NOCASE"
    )
    fun getAllSorted(columnName: String): Flow<List<CharacterDb>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun get(id: Int): CharacterDb?

    @Insert
    suspend fun insert(characterDb: CharacterDb)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(charactersDb: List<CharacterDb>)

    @Update
    suspend fun update(characterDb: CharacterDb)

    @Delete
    suspend fun delete(characterDb: CharacterDb)

    @Query("DELETE FROM characters")
    suspend fun clear()
}
