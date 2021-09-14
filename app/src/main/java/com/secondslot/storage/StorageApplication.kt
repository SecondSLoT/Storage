package com.secondslot.storage

import android.app.Application
import com.secondslot.storage.data.db.di.DbModule
import com.secondslot.storage.di.AppComponent
import com.secondslot.storage.di.DaggerAppComponent
import com.secondslot.storage.di.PrefsModule

class StorageApplication : Application() {

    companion object {
        private lateinit var appComponent: AppComponent

        fun getComponent(): AppComponent {
            return appComponent
        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .dbModule(DbModule(this))
            .prefsModule(PrefsModule(this))
            .build()
    }
}
