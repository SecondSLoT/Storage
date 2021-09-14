package com.secondslot.storage.di

import com.secondslot.storage.data.db.di.DbModule
import com.secondslot.storage.data.repository.RepositoryImpl
import com.secondslot.storage.features.addentity.vm.AddEntityViewModel
import com.secondslot.storage.features.storagelist.vm.StorageListViewModel
import dagger.Component

@ApplicationScope
@Component(modules = [DbModule::class, PrefsModule::class])
interface AppComponent {

    fun injectStorageListViewModel(viewModel: StorageListViewModel)

    fun injectAddEntityViewModel(viewModel: AddEntityViewModel)

    fun injectRepository(repository: RepositoryImpl)
}
