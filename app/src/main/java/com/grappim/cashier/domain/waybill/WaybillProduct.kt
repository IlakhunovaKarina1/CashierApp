package com.grappim.cashier.domain.waybill

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class WaybillProduct(
    val amount: BigDecimal,
    val barcode: String,
    val createdOn: String,
    val id: Int,
    val name: String,
    val productId: Int,
    val purchasePrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val updatedOn: String,
    val waybillId: Int
) : Parcelable