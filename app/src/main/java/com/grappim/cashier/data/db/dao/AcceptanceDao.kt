package com.grappim.cashier.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.grappim.cashier.data.db.entity.AcceptanceDB
import com.grappim.cashier.data.db.entity.acceptanceTable

@Dao
interface AcceptanceDao : BaseDao<AcceptanceDB> {

    @Query("SELECT * FROM $acceptanceTable")
    suspend fun getAcceptanceList(): List<AcceptanceDB>

    @Query("SELECT * FROM $acceptanceTable ORDER BY dateMillis DESC")
    fun getAcceptanceListPaging(): PagingSource<Int, AcceptanceDB>
}