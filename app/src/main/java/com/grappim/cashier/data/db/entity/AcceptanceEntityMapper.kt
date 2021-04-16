package com.grappim.cashier.data.db.entity

import com.grappim.cashier.domain.acceptance.Acceptance

object AcceptanceEntityMapper {

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