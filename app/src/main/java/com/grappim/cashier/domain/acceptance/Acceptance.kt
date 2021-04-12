package com.grappim.cashier.domain.acceptance

import com.grappim.cashier.ui.acceptance.AcceptanceStatus
import java.math.BigDecimal

data class Acceptance(
    val id: String,
    val vendorName: String,
    val date: String,
    val status: AcceptanceStatus,
    val toPay: BigDecimal? = null,
    val paidSum: BigDecimal? = null,
)