package com.grappim.cashier.ui.products

import com.grappim.cashier.data.db.entity.Product

interface ProductsClickListener {

    fun onProductClick(product: Product)
}