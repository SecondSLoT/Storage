package com.secondslot.storage.data.db.di

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import com.secondslot.storage.data.db.AppDatabaseHelper
import com.secondslot.storage.data.db.AppDatabaseRoom
import com.secondslot.storage.data.db.dao.CharacterDao
import com.secondslot.storage.data.db.dao.CharacterDaoCursor
import com.secondslot.storage.di.ApplicationScope
import dagger.Module
import dagger.Provides

private const val DATABASE_NAME = "AppDatabase"

@Module
class DbModule(private val context: Context) {

    @ApplicationScope
    @Provides
    fun provideAppDatabase(): AppDatabaseRoom {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabaseRoom::class.java,
            DATABASE_NAME
        )
            .build()
    }

    @Provides
    fun provideCharacterDao(db: AppDatabaseRoom): CharacterDao {
        return db.characterDao
    }

    @ApplicationScope
    @Provides
    fun provideAppDatabaseCursor(): SQLiteDatabase = AppDatabaseHelper(context).writableDatabase

    @Provides
    fun provideCharacterDaoCursor(db: SQLiteDatabase): CharacterDaoCursor {
        return CharacterDaoCursor(db)
    }
}