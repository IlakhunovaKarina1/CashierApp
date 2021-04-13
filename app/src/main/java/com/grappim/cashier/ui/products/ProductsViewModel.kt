package com.grappim.cashier.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.domain.products.GetCategoryListUseCase
import com.grappim.cashier.domain.products.GetProductsByQueryUseCase
import com.zhuinden.livedatacombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsByQueryUseCase: GetProductsByQueryUseCase,
    private val getCategoryListUseCase: GetCategoryListUseCase
) : ViewModel() {

    private val _categories: MutableLiveData<List<Category>> = MutableLiveData()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _query = MutableLiveData<String>()

    private val _selectedCategory = MutableLiveData<Category>()

    val products: LiveData<List<Product>> = Transformations.switchMap(
        combineTuple(
            _query,
            _selectedCategory
        )
    ) { (query, category) ->
        getProductsByQueryUseCase.invoke(category, query!!)
            .asLiveData(viewModelScope.coroutineContext)
    }

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            getCategoryListUseCase.invoke()
                .onFailure {

                }.onSuccess {
                    _categories.value = it
                }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _query.value = query
        }
    }

    fun setCategory(category: Category) {
        viewModelScope.launch {
            _selectedCategory.value = category
        }
    }

}