package com.grappim.cashier.api

import com.grappim.cashier.domain.login.LoginDTO
import com.grappim.cashier.domain.login.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CashierApi {

    @POST("merch/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginDTO
}