package com.grappim.cashier.domain.acceptance

import androidx.paging.PagingData
import com.grappim.cashier.domain.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAcceptanceListPagingUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    operator fun invoke(): Flow<PagingData<Acceptance>> =
        generalRepository.getAcceptanceListPaging()
}