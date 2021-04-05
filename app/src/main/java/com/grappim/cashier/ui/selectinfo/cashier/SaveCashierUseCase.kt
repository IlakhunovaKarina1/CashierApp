package com.grappim.cashier.ui.selectinfo.cashier

import com.grappim.cashier.core.domain.Cashier
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class SaveCashierUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(cashier: Cashier) = generalRepository.saveCashier(cashier)
}