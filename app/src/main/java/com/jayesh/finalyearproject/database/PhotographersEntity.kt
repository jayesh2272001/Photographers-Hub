package com.jayesh.finalyearproject.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photographers")
data class PhotographersEntity(
    @ColumnInfo(name = "photographers_id") @PrimaryKey var photographersId: String,
    @ColumnInfo(name = "photographers_name") var photographersName: String
)
