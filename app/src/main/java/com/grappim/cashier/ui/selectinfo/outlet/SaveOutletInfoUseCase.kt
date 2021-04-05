package com.grappim.cashier.ui.selectinfo.outlet

import com.grappim.cashier.core.domain.Outlet
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class SaveOutletInfoUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(outlet: Outlet) = generalRepository.saveOutlet(outlet)
}