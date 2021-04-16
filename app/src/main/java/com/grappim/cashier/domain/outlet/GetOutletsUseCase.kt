package com.grappim.cashier.domain.outlet

import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.repository.SelectInfoRepository
import javax.inject.Inject

class GetOutletsUseCase @Inject constructor(
    private val selectInfoRepository: SelectInfoRepository
) {

    suspend operator fun invoke(): Either<Throwable, List<Outlet>> =
        selectInfoRepository.getOutlets()
}