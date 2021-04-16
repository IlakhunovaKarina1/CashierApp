package com.grappim.cashier.domain.sales

import com.grappim.cashier.data.db.entity.ProductEntity
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class RemoveProductUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(productEntity: ProductEntity) =
        generalRepository.removeBasketProduct(productEntity)
}