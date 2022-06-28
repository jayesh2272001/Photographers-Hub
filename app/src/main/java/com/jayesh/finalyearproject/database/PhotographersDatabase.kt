package com.jayesh.finalyearproject.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PhotographersEntity::class], version = 1)
abstract class PhotographersDatabase : RoomDatabase() {

    abstract fun photographersDao(): PhotographersDao
}