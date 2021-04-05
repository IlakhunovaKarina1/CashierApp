package com.grappim.cashier.ui.sales

import com.grappim.cashier.data.db.entity.Product

interface BasketProductClickListener {
    fun addProduct(product: Product)
    fun removeProduct(product: Product)
}