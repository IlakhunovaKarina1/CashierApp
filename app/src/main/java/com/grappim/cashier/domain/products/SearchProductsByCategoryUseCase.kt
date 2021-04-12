package com.grappim.cashier.domain.products

import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class SearchProductsByCategoryUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(category: Category): List<Product> =
        generalRepository.getProductsByCategory(category)
}