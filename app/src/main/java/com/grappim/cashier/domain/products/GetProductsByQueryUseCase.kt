package com.grappim.cashier.domain.products

import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.data.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByQueryUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {
    operator fun invoke(
        category: Category?,
        query: String
    ): Flow<List<Product>> = generalRepository.getProductsByQuery(category, query)
}