package com.grappim.cashier.core.domain

import java.math.BigDecimal

data class Acceptance(
    val id: String,
    val vendorName: String,
    val date: String,
    val status: String,
    val toPay: BigDecimal? = null,
    val paidSum: BigDecimal? = null,
)