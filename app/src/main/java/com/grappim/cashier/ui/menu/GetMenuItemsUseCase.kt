package com.grappim.cashier.ui.menu

import com.grappim.cashier.data.repository.GeneralRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuItemsUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    fun getMenuItems(): Flow<List<MenuItem>> = generalRepository.getMenuItems()
}