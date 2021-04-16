package com.grappim.cashier.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

const val basketEntityTableName = "basket_table"

@Entity(
    tableName = basketEntityTableName
)
@Parcelize
data class BasketProductEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val barcode: String,
    var basketCount: BigDecimal,
    val amount: BigDecimal,
    val sellingPrice: BigDecimal,

    val categoryId: String? = null
) : Parcelable