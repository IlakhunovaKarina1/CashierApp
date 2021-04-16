package com.grappim.cashier.ui.sales

import com.grappim.cashier.data.db.entity.ProductEntity

interface BasketProductClickListener {
    fun addProduct(productEntity: ProductEntity)
    fun removeProduct(productEntity: ProductEntity)
}