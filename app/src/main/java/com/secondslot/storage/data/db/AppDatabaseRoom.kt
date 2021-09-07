package com.secondslot.storage.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.secondslot.storage.data.db.dao.CharacterDao
import com.secondslot.storage.data.db.model.CharacterDb

@Database(entities = [CharacterDb::class], version = 1)
abstract class AppDatabaseRoom : RoomDatabase() {

    abstract val characterDao: CharacterDao
}