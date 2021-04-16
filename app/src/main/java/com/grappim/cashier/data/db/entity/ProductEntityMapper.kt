package com.grappim.cashier.data.db.entity

import com.grappim.cashier.core.utils.ProductUnit
import com.grappim.cashier.data.db.entity.ProductEntityMapper.toDTO
import com.grappim.cashier.data.remote.model.product.ProductDTO

object ProductEntityMapper {

    fun ProductEntity.toBasketProduct(): BasketProductEntity =
        BasketProductEntity(
            id = this.id,
            name = this.name,
            basketCount = this.basketCount,
            sellingPrice = this.sellingPrice,
            categoryId = this.categoryId,
            amount = this.amount,
            barcode = this.barcode
        )

    fun ProductEntity.toDTO(): ProductDTO =
        ProductDTO(
            id = this.id,
            barcode = this.barcode,
            name = this.name,
            stockId = this.stockId,
            amount = this.amount,
            unit = this.unit.value,
            purchasePrice = this.purchasePrice,
            sellingPrice = this.sellingPrice,
            merchantId = this.merchantId,
            createdOn = this.createdOn,
            updatedOn = this.updatedOn
        )

    fun ProductDTO.toEntity(): ProductEntity =
        ProductEntity(
            id = this.id,
            barcode = this.barcode,
            name = this.name,
            stockId = this.stockId,
            amount = this.amount,
            unit = ProductUnit.getProductUnitByValue(this.unit),
            purchasePrice = this.purchasePrice,
            sellingPrice = this.sellingPrice,
            merchantId = this.merchantId,
            createdOn = this.createdOn,
            updatedOn = this.updatedOn
        )

}