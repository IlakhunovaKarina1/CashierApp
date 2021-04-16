package com.grappim.cashier.domain.cashier

import com.grappim.cashier.domain.repository.SelectInfoRepository
import javax.inject.Inject

class SaveCashierUseCase @Inject constructor(
    private val selectInfoRepository: SelectInfoRepository
) {

    suspend operator fun invoke(cashier: Cashier) = selectInfoRepository.saveCashier(cashier)
}