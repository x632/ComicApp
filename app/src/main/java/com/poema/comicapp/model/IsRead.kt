package com.poema.comicapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "read")
class IsRead(
    @PrimaryKey(autoGenerate = false) val id: Int,
)
