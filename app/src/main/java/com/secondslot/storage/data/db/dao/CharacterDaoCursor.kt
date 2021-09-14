package com.secondslot.storage.data.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.secondslot.storage.data.db.CharacterTable
import com.secondslot.storage.data.db.model.CharacterDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "CharacterDaoCursor"

class CharacterDaoCursor @Inject constructor(
    private val db: SQLiteDatabase
) : CharacterDao {

    private var currentSortColumn = "id"
    private var dbDataChangedListener: ((List<CharacterDb>) -> Unit)? = null

    override fun getAllSorted(columnName: String): Flow<List<CharacterDb>> {
        currentSortColumn = columnName

        return listenDbDataChanges()
    }

    private fun listenDbDataChanges(): Flow<List<CharacterDb>> = callbackFlow {

        val listener: (List<CharacterDb>) -> Unit = {
            trySend(it)
        }

        dbDataChangedListener = listener

        CoroutineScope(Dispatchers.IO).launch {
            dbDataChangedListener?.invoke(getAllDataFromDb())
        }

        awaitClose { dbDataChangedListener = null }
    }

    private fun getAllDataFromDb(): List<CharacterDb> {
        Log.d(TAG, "getAllDataFromDb(), thread = " + Thread.currentThread().name)
        val charactersDb = mutableListOf<CharacterDb>()

        val cursor = db.rawQuery(
            "SELECT * FROM ${CharacterTable.TABLE_NAME} ORDER BY " +
                "CASE WHEN ? = '${CharacterTable.NAME}' THEN name " +
                "WHEN ? = '${CharacterTable.LOCATION}' THEN location " +
                "WHEN ? = '${CharacterTable.QUOTE}' THEN quote END COLLATE NOCASE",
            arrayOf(currentSortColumn, currentSortColumn, currentSortColumn)
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

        withContext(Dispatchers.IO) {
            val values = getContentValues(characterDb)
            db.insert(CharacterTable.TABLE_NAME, null, values)
            dbDataChangedListener?.invoke(getAllDataFromDb())
        }
    }

    override suspend fun insertAll(charactersDb: List<CharacterDb>) {

        withContext(Dispatchers.IO) {
            for (item in charactersDb) {
                db.insert(CharacterTable.TABLE_NAME, null, getContentValues(item))
            }
            dbDataChangedListener?.invoke(getAllDataFromDb())
        }
    }

    override suspend fun update(characterDb: CharacterDb) {

        withContext(Dispatchers.IO) {
            val values = getContentValues(characterDb)
            db.update(
                CharacterTable.TABLE_NAME,
                values,
                "id = ?",
                arrayOf(characterDb.id.toString())
            )
            dbDataChangedListener?.invoke(getAllDataFromDb())
        }
    }

    override suspend fun delete(characterDb: CharacterDb) {

        withContext(Dispatchers.IO) {
            db.delete(
                CharacterTable.TABLE_NAME,
                "id = ?",
                arrayOf(characterDb.id.toString())
            )
            dbDataChangedListener?.invoke(getAllDataFromDb())
        }
    }

    override suspend fun clear() {

        withContext(Dispatchers.IO) {
            db.delete(
                CharacterTable.TABLE_NAME,
                null,
                null
            )
            dbDataChangedListener?.invoke(getAllDataFromDb())
        }
    }

    private fun getContentValues(characterDb: CharacterDb): ContentValues {
        val values = ContentValues()
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
