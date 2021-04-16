package com.grappim.cashier.ui.products.create

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.core.utils.ProductUnit
import com.grappim.cashier.domain.products.CreateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val createProductUseCase: CreateProductUseCase
) : ViewModel() {

    @MainThread
    fun createProduct(
        name: String,
        barcode: String,
        sellingPrice: BigDecimal,
        purchasePrice: BigDecimal,
        amount: BigDecimal,
        unit: ProductUnit,
    ) {
        viewModelScope.launch {
            createProductUseCase.invoke(
                name = name,
                barcode = barcode,
                sellingPrice = sellingPrice,
                purchasePrice = purchasePrice,
                amount = amount,
                unit = unit
            ).onFailure {

            }.onSuccess {

            }
        }
    }
}