package com.grappim.cashier.api

import com.grappim.cashier.core.network.RequestWithAuthToken
import com.grappim.cashier.data.remote.model.login.LoginResponseDTO
import com.grappim.cashier.data.remote.model.login.LoginRequestDTO
import com.grappim.cashier.data.remote.model.login.SendTokenToRefreshRequestDTO
import com.grappim.cashier.data.remote.model.outlet.GetOutletsResponseDTO
import com.grappim.cashier.data.remote.model.product.ProductDTO
import com.grappim.cashier.data.remote.model.product.CreateProductRequestDTO
import com.grappim.cashier.data.remote.model.product.GetProductsRequestDTO
import com.grappim.cashier.data.remote.model.product.UpdateProductResponseDTO
import com.grappim.cashier.data.remote.model.product.ProductIdResponseDTO
import com.grappim.cashier.data.remote.model.cashbox.GetCashBoxListRequestDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CashierApi {

    @POST("merch/refresh")
    suspend fun refreshToken(
        @Body request: SendTokenToRefreshRequestDTO
    ): SendTokenToRefreshRequestDTO

    @POST("merch/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDTO
    ): LoginResponseDTO

    @GET("stocks/list/{merchantId}")
    @RequestWithAuthToken
    suspend fun getStocks(
        @Path("merchantId") merchantId: String
    ): GetOutletsResponseDTO

    @GET("product/{productId}")
    @RequestWithAuthToken
    suspend fun getProductById(
        @Path("productId") productId: String
    ): ProductDTO

    @POST("product/")
    @RequestWithAuthToken
    suspend fun createProduct(
        @Body createProduct: CreateProductRequestDTO
    ): ProductIdResponseDTO

    @PUT("product/")
    @RequestWithAuthToken
    suspend fun updateProduct(
        @Body product: ProductDTO
    ): UpdateProductResponseDTO

    @DELETE("product/{productId}")
    @RequestWithAuthToken
    suspend fun deleteProduct(
        @Path("productId") id: String
    ): ProductIdResponseDTO

    @POST("product/filter")
    @RequestWithAuthToken
    suspend fun getProducts(
        @Body getProductsRequestDTO: GetProductsRequestDTO
    )

    @POST("cashbox/list")
    @RequestWithAuthToken
    suspend fun getCashBoxList(
        @Body getCashBoxListRequestDTO: GetCashBoxListRequestDTO
    )
}