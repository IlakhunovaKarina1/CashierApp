package com.grappim.cashier.ui.waybill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.domain.waybill.Waybill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WaybillSharedViewModel @Inject constructor(

) : ViewModel() {

    private val _waybill = MutableLiveData<Waybill>()
    val waybill: LiveData<Waybill>
        get() = _waybill

    fun setCurrentWaybill(waybill: Waybill) {
        viewModelScope.launch {
            _waybill.value = waybill
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("cashier asd ${this::class.java.simpleName} onCleared")
    }
}