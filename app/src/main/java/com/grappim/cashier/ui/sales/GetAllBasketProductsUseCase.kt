package com.grappim.cashier.ui.sales

import com.grappim.cashier.data.db.entity.BasketProduct
import com.grappim.cashier.data.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBasketProductsUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

     operator fun invoke(): Flow<List<BasketProduct>> = generalRepository.getAllBasketProducts()

}