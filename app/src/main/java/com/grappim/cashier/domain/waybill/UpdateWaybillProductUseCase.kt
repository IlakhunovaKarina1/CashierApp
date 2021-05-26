package com.grappim.cashier.domain.waybill

import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.domain.repository.WaybillRepository
import java.math.BigDecimal
import javax.inject.Inject

class UpdateWaybillProductUseCase @Inject constructor(
    private val waybillRepository: WaybillRepository
) {

    suspend operator fun invoke(
        waybillId: Int,
        barcode: String,
        name: String,
        purchasePrice: BigDecimal,
        sellingPrice: BigDecimal,
        amount: BigDecimal,
        productId: Int
    ): Either<Throwable, Unit> {
        val params = UpdateWaybillProductParams(
            waybillId = waybillId,
            barcode = barcode,
            name = name,
            purchasePrice = purchasePrice,
            sellingPrice = sellingPrice,
            amount = amount,
            productId = productId
        )
        return waybillRepository.updateWaybillProduct(params)
    }

    data class UpdateWaybillProductParams(
        val waybillId: Int,
        val barcode: String,
        val name: String,
        val purchasePrice: BigDecimal,
        val sellingPrice: BigDecimal,
        val amount: BigDecimal,
        val productId: Int
    )
}