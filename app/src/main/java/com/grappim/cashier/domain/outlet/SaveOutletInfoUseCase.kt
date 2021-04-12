package com.grappim.cashier.domain.outlet

import com.grappim.cashier.domain.outlet.Outlet
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class SaveOutletInfoUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(outlet: Outlet) = generalRepository.saveOutlet(outlet)
}