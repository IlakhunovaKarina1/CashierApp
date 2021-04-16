package com.grappim.cashier.domain.sales

import com.grappim.cashier.data.db.entity.BasketProductEntity
import com.grappim.cashier.data.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBasketProductsUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

     operator fun invoke(): Flow<List<BasketProductEntity>> = generalRepository.getAllBasketProducts()

}