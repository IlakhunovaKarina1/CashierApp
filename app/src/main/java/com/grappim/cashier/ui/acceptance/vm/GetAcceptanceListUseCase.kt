package com.grappim.cashier.ui.acceptance.vm

import com.grappim.cashier.core.domain.Acceptance
import com.grappim.cashier.data.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAcceptanceListUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    operator fun invoke(): Flow<List<Acceptance>> = generalRepository.getAcceptanceList()
}