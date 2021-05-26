package com.grappim.cashier.ui.waybill.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.core.platform.SingleLiveEvent
import com.grappim.cashier.domain.waybill.ConductWaybillUseCase
import com.grappim.cashier.domain.waybill.GetWaybillProductsUseCase
import com.grappim.cashier.domain.waybill.RollbackWaybillUseCase
import com.grappim.cashier.domain.waybill.Waybill
import com.grappim.cashier.domain.waybill.WaybillProduct
import com.grappim.cashier.ui.waybill.WaybillStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WaybillDetailsViewModel @Inject constructor(
    private val waybillProductsUseCase: GetWaybillProductsUseCase,
    private val conductWaybillUseCase: ConductWaybillUseCase,
    private val rollbackWaybillUseCase: RollbackWaybillUseCase
) : ViewModel() {

    private val _waybillId = MutableLiveData<Int>()

    private val _waybill = SingleLiveEvent<Waybill>()
    val waybill: LiveData<Waybill>
        get() = _waybill

    private val _waybillUpdate = MutableLiveData<Resource<Waybill>>()
    val waybillUpdate: LiveData<Resource<Waybill>>
        get() = _waybillUpdate

    val products: Flow<PagingData<WaybillProduct>> =
        _waybillId.asFlow()
            .distinctUntilChanged()
            .flatMapLatest {
                waybillProductsUseCase.getProducts(it)
            }

    fun setWaybillId(waybillId: Int) {
        _waybillId.value = waybillId
    }

    fun setWaybill(waybill: Waybill) {
        _waybill.value = waybill
    }

    fun setReservedDate(date: String) {
        _waybill.value = _waybill.value?.copy(reservedTime = date)
    }

    fun updateWaybill() {
        _waybillUpdate.value = Resource.Loading
        when (_waybill.value!!.status) {
            WaybillStatus.DRAFT -> {
                viewModelScope.launch {
                    conductWaybillUseCase.invoke(_waybillId.value!!)
                        .onSuccess {
                            _waybillUpdate.value = Resource.Success(it)
                        }.onFailure {
                            _waybillUpdate.value = Resource.Error(it)
                        }
                }
            }
            WaybillStatus.ACTIVE -> {
                viewModelScope.launch {
                    rollbackWaybillUseCase.invoke(_waybillId.value!!)
                        .onSuccess {
                            _waybillUpdate.value = Resource.Success(it)
                        }.onFailure {
                            _waybillUpdate.value = Resource.Error(it)
                        }
                }
            }
        }
    }

    override fun onCleared() {
        Timber.d("cashier asd ${this::class.java.simpleName} onCleared")
        super.onCleared()
    }
}