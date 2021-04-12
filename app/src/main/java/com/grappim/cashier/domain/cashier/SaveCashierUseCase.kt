package com.grappim.cashier.domain.cashier

import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class SaveCashierUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(cashier: Cashier) = generalRepository.saveCashier(cashier)
}