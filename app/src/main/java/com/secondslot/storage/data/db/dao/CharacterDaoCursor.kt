package com.secondslot.storage.data.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.secondslot.storage.data.db.CharacterTable
import com.secondslot.storage.data.db.model.CharacterDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "CharacterDaoCursor"

class CharacterDaoCursor @Inject constructor(
    private val db: SQLiteDatabase
) : CharacterDao() {

    private var currentSortColumn = "id"
    private var dbDataChangedListener: ((Unit) -> Unit)? = null

    override fun getAllSorted(columnName: String): Flow<List<CharacterDb>> {
        currentSortColumn = columnName

        return listenDbDataChanges()
    }

    private fun listenDbDataChanges(): Flow<List<CharacterDb>> = callbackFlow {

        val listener: (Unit) -> Unit = {
            trySend(getAllDataFromDb())
        }

        dbDataChangedListener = listener
        dbDataChangedListener?.invoke(Unit)

        awaitClose { dbDataChangedListener = null }
    }

    private fun getAllDataFromDb(): List<CharacterDb> {
        Log.d(TAG, "getAllDataFromDb using CharacterDaoCursor")
        val charactersDb = mutableListOf<CharacterDb>()

        val cursor = db.query(
            CharacterTable.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            currentSortColumn
        )

        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    charactersDb.add(getCharacterDbFromCursor(cursor))
                } while (cursor.moveToNext())
            }
        }

        return charactersDb
    }

    override suspend fun get(id: Int): CharacterDb? {
        Log.d(TAG, "get()")
        var characterDb: CharacterDb? = null

        withContext(Dispatchers.IO) {
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
                }
            }
        }

        return characterDb
    }

    override suspend fun insert(characterDb: CharacterDb) {
        Log.d(TAG, "insert()")

        withContext(Dispatchers.IO) {
            val values = getContentValues(characterDb)
            db.insert(CharacterTable.TABLE_NAME, null, values)
        }

        dbDataChangedListener?.invoke(Unit)
    }

    override suspend fun insertAll(charactersDb: List<CharacterDb>) {
        Log.d(TAG, "insertAll()")

        withContext(Dispatchers.IO) {
            for (item in charactersDb) {
                db.insert(CharacterTable.TABLE_NAME, null, getContentValues(item))
            }
        }

        dbDataChangedListener?.invoke(Unit)
    }

    override suspend fun update(characterDb: CharacterDb) {
        Log.d(TAG, "update()")

        withContext(Dispatchers.IO) {
            val values = getContentValues(characterDb)
            db.update(
                CharacterTable.TABLE_NAME,
                values,
                "id = ?",
                arrayOf(characterDb.id.toString())
            )
        }

        dbDataChangedListener?.invoke(Unit)
    }

    override suspend fun delete(characterDb: CharacterDb) {
        Log.d(TAG, "delete() id = ${characterDb.id}")

        withContext(Dispatchers.IO) {
            db.delete(
                CharacterTable.TABLE_NAME,
                "id = ?",
                arrayOf(characterDb.id.toString())
            )
        }

        dbDataChangedListener?.invoke(Unit)
    }

    override suspend fun clear() {

        withContext(Dispatchers.IO) {
            db.delete(
                CharacterTable.TABLE_NAME,
                null,
                null
            )
        }

        dbDataChangedListener?.invoke(Unit)
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