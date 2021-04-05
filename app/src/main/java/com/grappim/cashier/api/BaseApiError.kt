package com.grappim.cashier.api

data class BaseApiError(
    val system: String,
    val status: String,
    val code: String,
    val message: String,
    val developerMessage: String
)