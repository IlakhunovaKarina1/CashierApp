package com.grappim.cashier.ui.selectinfo.outlet.vm

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.R
import com.grappim.cashier.core.domain.Outlet
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.core.platform.SingleLiveEvent
import com.grappim.cashier.ui.selectinfo.outlet.OutletProgressItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectOutletViewModel @Inject constructor(
    private val getOutletsUseCase: GetOutletsUseCase,
    private val saveOutletInfoUseCase: SaveOutletInfoUseCase
) : ViewModel() {

    private val _outlets: SingleLiveEvent<Resource<List<Outlet>>> = SingleLiveEvent()
    val outlets: LiveData<Resource<List<Outlet>>>
        get() = _outlets

    val outletProgress: List<OutletProgressItem> = getOutletProgressItems()

    init {
        getOutlets()
    }

    @MainThread
    fun getOutlets() {
        viewModelScope.launch {
            _outlets.value = Resource.Loading
            getOutletsUseCase.invoke()
                .onFailure {
                    _outlets.value = Resource.Error(it)
                }.onSuccess {
                    _outlets.value = Resource.Success(it)
                }
        }
    }

    fun saveOutlet(outlet: Outlet) {
        viewModelScope.launch {
            saveOutletInfoUseCase.invoke(outlet)
        }
    }

    private fun getOutletProgressItems(): List<OutletProgressItem> =
        listOf(
            OutletProgressItem(R.string.outlet_selecting, true),
            OutletProgressItem(R.string.outlet_checkout, false),
            OutletProgressItem(R.string.title_empty, false)
        )
}