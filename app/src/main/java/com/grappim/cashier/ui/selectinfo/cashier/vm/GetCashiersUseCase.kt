package com.grappim.cashier.ui.selectinfo.cashier.vm

import com.grappim.cashier.core.domain.Cashier
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class GetCashiersUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {
    suspend operator fun invoke(): Either<Throwable, List<Cashier>> =
        generalRepository.getCashiers()
}