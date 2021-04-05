package com.grappim.cashier.ui.products.vm

import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.db.entity.BasketProduct
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(): Either<Throwable, List<Product>> =
        generalRepository.getProducts()
}