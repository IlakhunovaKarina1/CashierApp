package com.grappim.cashier.core.domain

import com.grappim.cashier.ui.acceptance.vm.AcceptanceStatus
import java.math.BigDecimal

data class Acceptance(
    val id: String,
    val vendorName: String,
    val date: String,
    val status: AcceptanceStatus,
    val toPay: BigDecimal? = null,
    val paidSum: BigDecimal? = null,
)