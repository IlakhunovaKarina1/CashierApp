package com.grappim.cashier.domain.waybill

import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.domain.repository.WaybillRepository
import javax.inject.Inject

class RollbackWaybillUseCase @Inject constructor(
    private val waybillRepository: WaybillRepository
) {

    suspend operator fun invoke(
        waybillId: Int
    ): Either<Throwable, Waybill> =
        waybillRepository.rollbackWaybill(waybillId)
}