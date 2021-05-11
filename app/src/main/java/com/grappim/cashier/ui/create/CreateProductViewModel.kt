package com.grappim.cashier.ui.create

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.extensions.asBigDecimal
import com.grappim.cashier.core.extensions.bigDecimalOne
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.core.utils.ProductUnit
import com.grappim.cashier.di.modules.DecimalFormatSimple
import com.grappim.cashier.domain.products.CreateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val createProductUseCase: CreateProductUseCase,
    @DecimalFormatSimple private val dfSimple: DecimalFormat
) : ViewModel() {

    private val _unit = MutableLiveData<ProductUnit>(
        ProductUnit.values().first()
    )
    val unit: LiveData<ProductUnit>
        get() = _unit

    private val _quantity = MutableLiveData(
        dfSimple.format(bigDecimalOne())
    )
    val quantity: LiveData<String>
        get() = _quantity

    private val _createProduct = MutableLiveData<Resource<Unit>>()
    val createProduct: LiveData<Resource<Unit>>
        get() = _createProduct

    fun setUnit(unit: ProductUnit) {
        _unit.value = unit
    }

    fun minusQuantity() {
        val quantityToChange = _quantity.value!!.asBigDecimal()
        if (quantityToChange > bigDecimalOne()) {
            _quantity.value = dfSimple.format(quantityToChange - bigDecimalOne())
        }
    }

    fun plusQuantity() {
        val quantityToChange = _quantity.value!!.asBigDecimal()
        _quantity.value = dfSimple.format(quantityToChange + bigDecimalOne())
    }

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
            _createProduct.value = Resource.Loading
            createProductUseCase.invoke(
                name = name,
                barcode = barcode,
                sellingPrice = sellingPrice,
                purchasePrice = purchasePrice,
                amount = amount,
                unit = unit
            ).onFailure {
                _createProduct.value = Resource.Error(it)
            }.onSuccess {
                _createProduct.value = Resource.Success(it)
            }
        }
    }
}