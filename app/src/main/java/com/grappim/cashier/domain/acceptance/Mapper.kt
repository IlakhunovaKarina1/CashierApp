package com.grappim.cashier.domain.acceptance

import com.grappim.cashier.data.db.entity.AcceptanceEntity

object Mapper {

    fun AcceptanceEntity.toDomain(): Acceptance =
        Acceptance(
            id = this.id,
            vendorName = this.vendorName,
            date = this.date,
            status = this.status,
            toPay = this.toPay,
            paidSum = this.paidSum
        )

}