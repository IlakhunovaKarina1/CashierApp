package com.grappim.cashier.domain.repository

import androidx.paging.PagingData
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.db.entity.ProductEntity
import com.grappim.cashier.domain.products.GetProductByBarcodeUseCase
import com.grappim.cashier.domain.waybill.CreateWaybillProductUseCase
import com.grappim.cashier.domain.waybill.GetWaybillProductByBarcodeUseCase
import com.grappim.cashier.domain.waybill.UpdateWaybillProductUseCase
import com.grappim.cashier.domain.waybill.Waybill
import com.grappim.cashier.domain.waybill.WaybillProduct
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface WaybillRepository {

    fun getAcceptanceListPaging(): Flow<PagingData<Waybill>>
    suspend fun createDraftWaybill(): Either<Throwable, Waybill>

    fun getProducts(
        waybillId: Int
    ): Flow<PagingData<WaybillProduct>>

    suspend fun createWaybillProduct(
        params: CreateWaybillProductUseCase.CreateWaybillProductParams
    ): Either<Throwable, Unit>

    suspend fun getWaybillProductByBarcode(
        params: GetWaybillProductByBarcodeUseCase.GetWaybillProductByBarcodeParams
    ): Either<Throwable, WaybillProduct>

    suspend fun getProductByBarcode(
        params: GetProductByBarcodeUseCase.GetProductByBarcodeParams
    ): Either<Throwable, ProductEntity>

    suspend fun updateWaybillProduct(
        params: UpdateWaybillProductUseCase.UpdateWaybillProductParams
    ): Either<Throwable, Unit>

    suspend fun conductWaybill(
        waybillId: Int
    ): Either<Throwable, Waybill>

    suspend fun rollbackWaybill(
        waybillId: Int
    ): Either<Throwable, Waybill>

}