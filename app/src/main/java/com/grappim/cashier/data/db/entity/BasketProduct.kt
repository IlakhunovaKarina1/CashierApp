package com.grappim.cashier.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Entity
@Parcelize
data class BasketProduct(
    @PrimaryKey
    val uid: String,
    val name: String,
    var basketCount: BigDecimal,
    val stockCount: BigDecimal,
    val imageUrl: String,
    val price: BigDecimal,
    val categoryId: String? = null
) : Parcelable