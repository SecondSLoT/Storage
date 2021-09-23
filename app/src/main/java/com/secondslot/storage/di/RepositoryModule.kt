package com.secondslot.storage.di

import com.secondslot.storage.data.repository.RepositoryImpl
import com.secondslot.storage.domain.Repository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @ApplicationScope
    @Provides
    fun provideRepository(): Repository = RepositoryImpl()
}
