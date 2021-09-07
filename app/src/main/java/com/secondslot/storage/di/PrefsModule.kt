package com.secondslot.storage.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class PrefsModule(private val context: Context) {

    @Provides
    fun providePrefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}