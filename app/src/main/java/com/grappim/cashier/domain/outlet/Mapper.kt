package com.grappim.cashier.domain.outlet

object Mapper {

    fun List<OutletDTO>.toDomain(): List<Outlet> {
        val outletList = mutableListOf<Outlet>()
        this.forEach {
            outletList.add(it.toDomain())
        }
        return outletList
    }

    fun OutletDTO.toDomain(): Outlet =
        Outlet(
            name = this.stockName,
            merchantId = this.merchantId,
            stockId = this.stockId
        )
}