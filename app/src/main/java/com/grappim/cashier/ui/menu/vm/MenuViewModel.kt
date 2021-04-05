package com.grappim.cashier.ui.menu.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.storage.GeneralPrefsDataStore
import com.grappim.cashier.ui.menu.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    getMenuItemsUseCase: GetMenuItemsUseCase,
    generalPrefsDataStore: GeneralPrefsDataStore
) : ViewModel() {

    private val _cashierName: Flow<String> =
        generalPrefsDataStore.cashierName
    val cashierName: LiveData<String> = _cashierName
        .asLiveData(viewModelScope.coroutineContext)

    private val _menuItems: Flow<List<MenuItem>> = getMenuItemsUseCase.getMenuItems()
    val menuItems: LiveData<List<MenuItem>> = _menuItems
        .asLiveData(viewModelScope.coroutineContext)
}