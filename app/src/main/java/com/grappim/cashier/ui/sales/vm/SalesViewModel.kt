package com.grappim.cashier.ui.sales.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.domain.products.GetProductsUseCase
import com.grappim.cashier.domain.sales.AddProductToBasketUseCase
import com.grappim.cashier.domain.sales.GetAllBasketProductsUseCase
import com.grappim.cashier.domain.sales.RemoveProductUseCase
import com.grappim.cashier.domain.sales.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addProductToBasketUseCase: AddProductToBasketUseCase,
    private val removeProductUseCase: RemoveProductUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    getAllBasketProductsUseCase: GetAllBasketProductsUseCase
) : ViewModel() {

    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>>
        get() = _products

    private val _basketCount = getAllBasketProductsUseCase.invoke()
    val basketCount: LiveData<BigDecimal> =
        _basketCount.asLiveData(viewModelScope.coroutineContext).map { list ->
            list.map {
                it.basketCount
            }.sumOf {
                it
            }
        }

    init {
        getProducts()
    }

    fun addProductToBasket(basketProduct: Product) {
        viewModelScope.launch {
            addProductToBasketUseCase.invoke(basketProduct)
        }
    }

    fun removeProductFromBasket(basketProduct: Product) {
        viewModelScope.launch {
            removeProductUseCase.invoke(basketProduct)
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _products.value = searchProductsUseCase.invoke(query)
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            getProductsUseCase.invoke()
                .onFailure {

                }.onSuccess {
                    _products.value = it
                }
        }
    }

}