package com.grappim.cashier.ui.products.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.domain.products.GetCategoryListUseCase
import com.grappim.cashier.domain.products.GetProductsUseCase
import com.grappim.cashier.domain.products.SearchProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getCategoryListUseCase: GetCategoryListUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsByCategoryUseCase: SearchProductsByCategoryUseCase
) : ViewModel() {

    private val _categories: MutableLiveData<List<Category>> = MutableLiveData()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>>
        get() = _products

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            getCategoryListUseCase.invoke()
                .onFailure {

                }.onSuccess {
                    _categories.value = it
                    getProducts()
                }
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

    fun getProductsByCategory(category: Category) {
        viewModelScope.launch {
            _products.value = searchProductsByCategoryUseCase.invoke(category)
        }
    }
}