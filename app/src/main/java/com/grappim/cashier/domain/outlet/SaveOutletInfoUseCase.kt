package com.grappim.cashier.domain.outlet

import com.grappim.cashier.data.repository.SelectInfoRepository
import javax.inject.Inject

class SaveOutletInfoUseCase @Inject constructor(
    private val selectInfoRepository: SelectInfoRepository
) {

    suspend operator fun invoke(outlet: Outlet) = selectInfoRepository.saveOutlet(outlet)
}