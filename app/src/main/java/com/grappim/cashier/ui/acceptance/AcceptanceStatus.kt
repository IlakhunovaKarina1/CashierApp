package com.grappim.cashier.ui.acceptance

enum class AcceptanceStatus(val value: String) {
    STANDARD("standard"),
    PAID("paid");

    companion object {
        fun getStatusByValue(value: String): AcceptanceStatus =
            when (value) {
                STANDARD.value -> STANDARD
                PAID.value -> PAID
                else -> throw IllegalArgumentException("unit is null")
            }
    }
}