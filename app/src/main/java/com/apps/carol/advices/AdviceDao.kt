package com.apps.carol.advices

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.IGNORE

@Dao
interface AdviceDao {
    @Insert(onConflict = IGNORE)
    fun insert(advice: Advice)

    @Query("SELECT * FROM Advice")
    fun getAll() : List<Advice>

    @Delete
    fun delete(advice: Advice)
}