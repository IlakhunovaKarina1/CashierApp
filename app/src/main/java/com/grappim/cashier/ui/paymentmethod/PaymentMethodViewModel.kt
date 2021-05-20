package com.grappim.cashier.ui.paymentmethod

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.domain.products.GetBagProductsUseCase
import com.grappim.cashier.domain.sales.GetAllBasketProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val paymentMethodItemGenerator: PaymentMethodItemGenerator,
    getAllBasketProductsUseCase: GetAllBasketProductsUseCase
) : ViewModel() {

    private val _paymentItems = paymentMethodItemGenerator.paymentMethodItems
    val paymentItems = _paymentItems.asLiveData(viewModelScope.coroutineContext)

    private val _basketCount = getAllBasketProductsUseCase.invoke()
    val basketCount: LiveData<BigDecimal> =
        _basketCount.asLiveData(viewModelScope.coroutineContext).map { list ->
            list.map {
                it.basketCount
            }.sumOf {
                it
            }
        }
    private val _basketSum = getAllBasketProductsUseCase.invoke()
    val basketSum: LiveData<BigDecimal> =
        _basketSum.asLiveData(viewModelScope.coroutineContext).map { list ->
            list.map {
                it.sellingPrice * it.basketCount
            }.sumOf {
                it
            }
        }

    fun makePayment(paymentMethod: PaymentMethod) {
        when (paymentMethod.type) {
            PaymentMethodType.CARD -> {

            }
            PaymentMethodType.CASH -> {

            }
        }
    }
}