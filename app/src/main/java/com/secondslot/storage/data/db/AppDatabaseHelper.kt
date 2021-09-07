package com.secondslot.storage.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDatabaseHelper"
private const val CREATE_TABLE_SQL = "CREATE TABLE ${CharacterTable.TABLE_NAME} " +
        "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "${CharacterTable.NAME} TEXT NOT NULL, " +
        "${CharacterTable.LOCATION} TEXT NOT NULL, " +
        "${CharacterTable.QUOTE} TEXT NOT NULL);"

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade() called")
    }
}