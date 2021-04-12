package com.grappim.cashier.ui.selectinfo.cashier

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.R
import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.core.platform.SingleLiveEvent
import com.grappim.cashier.domain.cashier.GetCashiersUseCase
import com.grappim.cashier.domain.cashier.SaveCashierUseCase
import com.grappim.cashier.ui.selectinfo.outlet.OutletProgressItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCashierViewModel @Inject constructor(
    private val getCashiersUseCase: GetCashiersUseCase,
    private val saveCashierUseCase: SaveCashierUseCase
) : ViewModel() {

    private val _cashiers: SingleLiveEvent<Resource<List<Cashier>>> = SingleLiveEvent()
    val cashiers: LiveData<Resource<List<Cashier>>>
        get() = _cashiers

    val outletProgress: List<OutletProgressItem> = getOutletProgressItems()

    init {
        getCashiers()
    }

    fun saveCashier(cashier: Cashier) {
        viewModelScope.launch {
            saveCashierUseCase.invoke(cashier)
        }
    }

    @MainThread
    fun getCashiers() {
        viewModelScope.launch {
            _cashiers.value = Resource.Loading
            getCashiersUseCase.invoke()
                .onFailure {
                    _cashiers.value = Resource.Error(it)
                }.onSuccess {
                    _cashiers.value = Resource.Success(it)
                }
        }
    }

    private fun getOutletProgressItems(): List<OutletProgressItem> =
        listOf(
            OutletProgressItem(R.string.outlet_selecting, true),
            OutletProgressItem(R.string.outlet_checkout, true),
            OutletProgressItem(R.string.title_empty, true)
        )
}