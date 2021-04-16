package com.grappim.cashier.domain.products

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProductDTO(
    val id: String,
    val barcode: String,
    val name: String,
    @SerializedName("stock_id")
    val stockId: String,
    val amount: BigDecimal,
    val unit: String,
    @SerializedName("purchase_price")
    val purchasePrice: BigDecimal,
    @SerializedName("selling_price")
    val sellingPrice: BigDecimal,
    @SerializedName("merchant_id")
    val merchantId: String,
    @SerializedName("created_on")
    val createdOn: String,
    @SerializedName("updated_on")
    val updatedOn: String
)