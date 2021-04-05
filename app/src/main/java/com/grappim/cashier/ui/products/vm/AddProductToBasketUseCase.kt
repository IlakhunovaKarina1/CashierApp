package com.grappim.cashier.ui.products.vm

import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class AddProductToBasketUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(product: Product) =
        generalRepository.addBasketProduct(product)
}