package com.grappim.cashier.api

import com.grappim.cashier.core.network.RequestWithAuthToken
import com.grappim.cashier.domain.login.LoginDTO
import com.grappim.cashier.domain.login.LoginRequest
import com.grappim.cashier.domain.outlet.GetOutletResponse
import com.grappim.cashier.domain.outlet.Outlet
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CashierApi {

    @POST("merch/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginDTO

    @GET("stocks/list/{merchantId}")
    @RequestWithAuthToken
    suspend fun getStocks(
        @Path("merchantId") merchantId: String
    ): GetOutletResponse
}