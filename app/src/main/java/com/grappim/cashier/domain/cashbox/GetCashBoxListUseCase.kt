package com.grappim.cashier.domain.cashbox

import com.grappim.cashier.domain.repository.SelectInfoRepository
import javax.inject.Inject

class GetCashBoxListUseCase @Inject constructor(
    private val selectInfoRepository: SelectInfoRepository
) {
}