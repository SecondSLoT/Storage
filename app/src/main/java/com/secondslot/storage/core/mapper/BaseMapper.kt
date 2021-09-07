package com.secondslot.storage.core.mapper

interface BaseMapper<in A, out B> {

    fun map(type: A?): B
}