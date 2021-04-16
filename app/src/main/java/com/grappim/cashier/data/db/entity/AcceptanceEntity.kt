package com.grappim.cashier.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grappim.cashier.ui.acceptance.AcceptanceStatus
import java.math.BigDecimal

const val acceptanceTableName = "acceptance_table"

@Entity(
    tableName = acceptanceTableName
)
data class AcceptanceEntity(
    @PrimaryKey
    val id: String,
    val vendorName: String,
    val date: String,
    val dateMillis: Long,
    val status: AcceptanceStatus,
    val toPay: BigDecimal? = null,
    val paidSum: BigDecimal? = null,
)