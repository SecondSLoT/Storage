package com.secondslot.storage.features.addentity.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException


class AddEntityViewModelFactory(private val id: Int)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEntityViewModel::class.java)) {
            return AddEntityViewModel(id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}