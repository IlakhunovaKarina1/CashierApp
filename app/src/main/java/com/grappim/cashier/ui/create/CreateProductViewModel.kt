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
import com.grappim.cashier.core.utils.PriceCalculationsUtils
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
    private val priceCalculationsUtils: PriceCalculationsUtils,
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

    private val _sellingPrice = MutableLiveData<String>("0")
    val sellingPrice: LiveData<String>
        get() = _sellingPrice

    private val _purchasePrice = MutableLiveData<String>("0")
    val purchasePrice: LiveData<String>
        get() = _purchasePrice

    private val _markup = MutableLiveData<String>("0")
    val markup: LiveData<String>
        get() = _markup

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
    fun onSalesPriceChanged(salesPrice: String) {
        _sellingPrice.value = dfSimple.format(salesPrice.asBigDecimal())
        priceCalculationsUtils.calculateOnChangingSellingPrice(
            _sellingPrice.value!!.asBigDecimal(),
            _purchasePrice.value!!.asBigDecimal()
        ) { markup ->
            _markup.value = dfSimple.format(markup)
        }
    }

    @MainThread
    fun onCostPriceChanged(costPrice: String) {
        _purchasePrice.value = dfSimple.format(costPrice.asBigDecimal())
        val newSellingPrice = priceCalculationsUtils.calculateOnChangingMarkup(
            _purchasePrice.value!!.asBigDecimal(),
            _markup.value!!.asBigDecimal()
        )
        _sellingPrice.value = dfSimple.format(newSellingPrice)
    }

    @MainThread
    fun onExtraPriceChanged(extraPrice: String) {
        _markup.value = dfSimple.format(extraPrice.asBigDecimal())
        val newSellingPrice = priceCalculationsUtils.calculateOnChangingMarkup(
            _purchasePrice.value!!.asBigDecimal(),
            _markup.value!!.asBigDecimal()
        )
        _sellingPrice.value = dfSimple.format(newSellingPrice)
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