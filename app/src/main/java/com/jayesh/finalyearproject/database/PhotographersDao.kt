package com.jayesh.finalyearproject.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotographersDao {
    @Insert
    fun insertPhotographer(photographersEntity: PhotographersEntity)

    @Delete
    fun deletePhotographer(photographersEntity: PhotographersEntity)

    @Query("SELECT * FROM photographers")
    fun getAllPhotographers(): List<PhotographersEntity>

    @Query("SELECT * FROM photographers WHERE photographers_id= :photographersId")
    fun getPhotographerById(photographersId: String): PhotographersEntity

}