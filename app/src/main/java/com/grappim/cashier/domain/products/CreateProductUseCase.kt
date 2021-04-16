package com.grappim.cashier.domain.products

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
            createdOn = "",
            updatedOn = ""
        )
        return generalRepository.createProduct(params)
    }

    data class CreateProductParams(
        val name: String,
        val stockId: String,
        val merchantId: String,
        val unit: String,
        val purchasePrice: BigDecimal,
        val sellingPrice: BigDecimal,
        val amount: BigDecimal,
        val barcode: String,
        val createdOn: String,
        val updatedOn: String,
    )
}