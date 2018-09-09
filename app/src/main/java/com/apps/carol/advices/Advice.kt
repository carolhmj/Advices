package com.apps.carol.advices

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Advice(@PrimaryKey val slip_id : Int,
                  @ColumnInfo(name="advice") val advice : String)

data class Slip(val slip: Advice)