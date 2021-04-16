package com.grappim.cashier.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grappim.cashier.core.extensions.bigDecimalZero
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

const val productEntityTableName = "product_table"

@Entity(tableName = productEntityTableName)
@Parcelize
data class ProductEntity(
    @PrimaryKey
    val id: Long,
    val barcode: String,
    val name: String,
    val imageUrl: String,
    val price: BigDecimal,
    val stockCount: BigDecimal,
    var basketCount: BigDecimal = bigDecimalZero(),
    val categoryId: String? = null
) : Parcelable
