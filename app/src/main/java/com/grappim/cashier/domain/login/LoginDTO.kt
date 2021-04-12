package com.grappim.cashier.domain.login

import com.google.gson.annotations.SerializedName

data class LoginDTO(
    @SerializedName("merchant_id")
    val merchantId:String,
    val token:String
)