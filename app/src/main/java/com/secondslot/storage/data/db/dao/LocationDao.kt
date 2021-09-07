package com.secondslot.storage.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*

//@Dao
//interface LocationDao {
//
//    @Query("SELECT * FROM location")
//    suspend fun getAll(): LiveData<List<LocalLocation>>
//
//    @Query("SELECT * FROM character WHERE id = :id")
//    suspend fun get(id: Int): LocalLocation
//
//    @Insert
//    suspend fun insert(location: LocalLocation)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertAll(locations: List<LocalLocation>)
//
//    @Update
//    suspend fun update(location: LocalLocation)
//
//    @Delete
//    suspend fun delete(location: LocalLocation)
//}