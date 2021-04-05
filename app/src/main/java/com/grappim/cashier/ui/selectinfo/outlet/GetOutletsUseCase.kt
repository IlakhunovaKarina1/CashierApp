package com.grappim.cashier.ui.selectinfo.outlet

import com.grappim.cashier.core.domain.Outlet
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class GetOutletsUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(): Either<Throwable, List<Outlet>> =
        generalRepository.getOutlets()
}