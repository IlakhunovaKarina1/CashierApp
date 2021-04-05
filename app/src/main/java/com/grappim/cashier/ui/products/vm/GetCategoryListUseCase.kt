package com.grappim.cashier.ui.products.vm

import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class GetCategoryListUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(): Either<Throwable, List<Category>> =
        generalRepository.getCategories()
}