package com.secondslot.storage.data.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.secondslot.storage.data.db.CharacterTable
import com.secondslot.storage.data.db.model.CharacterDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

private const val TAG = "CharacterDaoCursor"

class CharacterDaoCursor @Inject constructor(
    private val db: SQLiteDatabase
) : CharacterDao() {

    private var sortColumn = "none"

    override fun getAllSorted(columnName: String): Flow<List<CharacterDb>> {
        sortColumn = columnName
        Log.d(TAG, "getAllSorted()")
        val charactersDb = mutableListOf<CharacterDb>()

        val cursor = db.query(
            CharacterTable.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            columnName
        )

        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    charactersDb.add(getCharacterDbFromCursor(cursor))
                } while (cursor.moveToNext())
            }
        }

        Log.d(TAG,"charactersDb size = ${charactersDb.size}")
        return flowOf(charactersDb)
    }

    override suspend fun get(id: Int): CharacterDb? {
        Log.d(TAG, "get()")
        var characterDb: CharacterDb

        val cursor = db.query(
            CharacterTable.TABLE_NAME,
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                characterDb = getCharacterDbFromCursor(cursor)
                getAllSorted(sortColumn)
                return characterDb
            }
        }
        return null
    }

    override suspend fun insert(characterDb: CharacterDb) {
        Log.d(TAG, "insert()")
        val values = getContentValues(characterDb)
        db.insert(CharacterTable.TABLE_NAME, null, values)
        getAllSorted(sortColumn)
    }

    override suspend fun insertAll(charactersDb: List<CharacterDb>) {
        Log.d(TAG, "insertAll()")
        for (item in charactersDb) {
            db.insert(CharacterTable.TABLE_NAME, null, getContentValues(item))
        }
        getAllSorted(sortColumn)
    }

    override suspend fun update(characterDb: CharacterDb) {
        Log.d(TAG, "update()")
        val values = getContentValues(characterDb)
        db.update(
            CharacterTable.TABLE_NAME,
            values,
            "id = ?",
            arrayOf(characterDb.id.toString())
        )
        getAllSorted(sortColumn)
    }

    override suspend fun delete(characterDb: CharacterDb) {
        Log.d(TAG, "delete() id = ${characterDb.id}")
        db.delete(
            CharacterTable.TABLE_NAME,
            "id = ?",
            arrayOf(characterDb.id.toString())
        )
        getAllSorted(sortColumn)
    }

    override suspend fun clear() {
        db.delete(
            CharacterTable.TABLE_NAME,
            null,
            null
        )
        getAllSorted(sortColumn)
    }

    private fun getContentValues(characterDb: CharacterDb): ContentValues {
        val values = ContentValues()
//        values.put(CharacterTable.ID, characterDb.id)
        values.put(CharacterTable.NAME, characterDb.name)
        values.put(CharacterTable.LOCATION, characterDb.location)
        values.put(CharacterTable.QUOTE, characterDb.quote)

        return values
    }

    private fun getCharacterDbFromCursor(cursor: Cursor): CharacterDb {
        return CharacterDb(
            id = cursor.getInt(cursor.getColumnIndex(CharacterTable.ID)),
            name = cursor.getString(cursor.getColumnIndex(CharacterTable.NAME)),
            location = cursor.getString(cursor.getColumnIndex(CharacterTable.LOCATION)),
            quote = cursor.getString(cursor.getColumnIndex(CharacterTable.QUOTE))
        )
    }
}