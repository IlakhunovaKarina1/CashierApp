package com.grappim.cashier.domain.acceptance

import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.data.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAcceptanceListUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    operator fun invoke(): Flow<List<Acceptance>> = generalRepository.getAcceptanceList()
}