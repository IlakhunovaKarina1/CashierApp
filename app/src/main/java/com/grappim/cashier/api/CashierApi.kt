package com.grappim.cashier.api

import com.grappim.cashier.core.network.RequestWithAuthToken
import com.grappim.cashier.domain.login.LoginDTO
import com.grappim.cashier.domain.login.LoginRequest
import com.grappim.cashier.domain.login.SendTokenToRefreshRequest
import com.grappim.cashier.domain.outlet.GetOutletResponse
import com.grappim.cashier.domain.products.ProductDTO
import com.grappim.cashier.domain.products.create.ProductChange
import com.grappim.cashier.domain.products.create.ProductIdResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CashierApi {

    @POST("merch/refresh")
    suspend fun refreshToken(
        @Body request: SendTokenToRefreshRequest
    ): SendTokenToRefreshRequest

    @POST("merch/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginDTO

    @GET("stocks/list/{merchantId}")
    @RequestWithAuthToken
    suspend fun getStocks(
        @Path("merchantId") merchantId: String
    ): GetOutletResponse

    @GET("product/{productId}")
    @RequestWithAuthToken
    suspend fun getProductById(
        @Path("productId") productId: String
    ): ProductDTO

    @POST("product/")
    @RequestWithAuthToken
    suspend fun createProduct(
        @Body product: ProductChange
    ): ProductIdResponse

    @PUT("product/")
    @RequestWithAuthToken
    suspend fun updateProduct(
        @Body product: ProductDTO
    ): ProductChange

    @DELETE("product/{productId}")
    suspend fun deleteProduct(
        @Path("productId") id: String
    ): ProductIdResponse
}