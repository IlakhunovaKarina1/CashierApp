package com.grappim.cashier.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.grappim.cashier.data.db.entity.AcceptanceEntity
import com.grappim.cashier.data.db.entity.acceptanceTableName

@Dao
interface AcceptanceDao : BaseDao<AcceptanceEntity> {

    @Query("SELECT * FROM $acceptanceTableName")
    suspend fun getAcceptanceList(): List<AcceptanceEntity>

    @Query("SELECT * FROM $acceptanceTableName ORDER BY dateMillis DESC")
    fun getAcceptanceListPaging(): PagingSource<Int, AcceptanceEntity>
}