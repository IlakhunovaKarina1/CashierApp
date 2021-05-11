package com.grappim.cashier.domain.products

import com.google.gson.annotations.SerializedName
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.core.storage.GeneralStorage
import com.grappim.cashier.core.utils.ProductUnit
import com.grappim.cashier.data.db.entity.ProductEntity
import com.grappim.cashier.data.db.entity.ProductEntityMapper.toDTO
import com.grappim.cashier.data.remote.model.product.CreateProductRequestDTO
import com.grappim.cashier.domain.repository.GeneralRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(
    private val generalStorage: GeneralStorage,
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(
        name: String,
        barcode: String,
        sellingPrice: BigDecimal,
        purchasePrice: BigDecimal,
        amount: BigDecimal,
        unit: ProductUnit,
    ): Either<Throwable, Unit> {
        val params = CreateProductParams(
            name = name,
            stockId = generalStorage.getStockId(),
            merchantId = generalStorage.getMerchantId(),
            unit = unit.value,
            purchasePrice = purchasePrice,
            sellingPrice = sellingPrice,
            amount = amount,
            barcode = barcode,
            createdOn = "0001-01-01T00:00:00Z",
            updatedOn = "2021-05-10T18:55:01.244424Z"
        )
        return generalRepository.createProduct(params)
    }

    data class CreateProductParams(
        val name: String,
        @SerializedName("stock_id")
        val stockId: String,
        @SerializedName("merchant_id")
        val merchantId: String,
        val unit: String,
        @SerializedName("purchase_price")
        val purchasePrice: BigDecimal,
        @SerializedName("selling_price")
        val sellingPrice: BigDecimal,
        val amount: BigDecimal,
        val barcode: String,
        @SerializedName("created_on")
        val createdOn: String,
        @SerializedName("updated_on")
        val updatedOn: String,
    )
}