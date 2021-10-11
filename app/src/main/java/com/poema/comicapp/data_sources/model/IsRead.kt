package com.poema.comicapp.data_sources.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "read")
class IsRead(
    @PrimaryKey(autoGenerate = false) val id: Int,
)
