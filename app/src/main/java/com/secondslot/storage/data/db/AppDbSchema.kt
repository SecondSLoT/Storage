package com.secondslot.storage.data.db

const val DATABASE_NAME = "AppDatabase"
const val DATABASE_VERSION = 1

class CharacterTable {
    companion object {
        const val TABLE_NAME = "characters"

        const val ID = "id"
        const val NAME = "name"
        const val LOCATION = "location"
        const val QUOTE = "quote"
    }
}