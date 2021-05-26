package com.grappim.cashier.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.grappim.cashier.api.WaybillApi
import com.grappim.cashier.core.executor.CoroutineContextProvider
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.core.functional.map
import com.grappim.cashier.core.storage.GeneralStorage
import com.grappim.cashier.data.db.dao.ProductsDao
import com.grappim.cashier.data.db.entity.ProductEntity
import com.grappim.cashier.data.paging.GetWaybillPagingSource
import com.grappim.cashier.data.paging.GetWaybillProductsPagingSource
import com.grappim.cashier.data.remote.BaseRepository
import com.grappim.cashier.data.remote.model.waybill.CreateWaybillProductRequestDTO
import com.grappim.cashier.data.remote.model.waybill.CreateWaybillRequestDTO
import com.grappim.cashier.data.remote.model.waybill.GetWaybillByBarcodeRequestDTO
import com.grappim.cashier.data.remote.model.waybill.PartialWaybill
import com.grappim.cashier.data.remote.model.waybill.PartialWaybillProductDTO
import com.grappim.cashier.data.remote.model.waybill.WaybillMapper.toDomain
import com.grappim.cashier.di.modules.QualifierWaybillApi
import com.grappim.cashier.domain.products.GetProductByBarcodeUseCase
import com.grappim.cashier.domain.repository.WaybillRepository
import com.grappim.cashier.domain.waybill.CreateWaybillProductUseCase
import com.grappim.cashier.domain.waybill.GetWaybillProductByBarcodeUseCase
import com.grappim.cashier.domain.waybill.UpdateWaybillProductUseCase
import com.grappim.cashier.domain.waybill.Waybill
import com.grappim.cashier.domain.waybill.WaybillProduct
import com.grappim.cashier.ui.waybill.WaybillStatus
import com.grappim.cashier.ui.waybill.WaybillType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaybillRepositoryImpl @Inject constructor(
    private val coroutineContextProvider: CoroutineContextProvider,
    @QualifierWaybillApi private val waybillApi: WaybillApi,
    private val getWaybillPagingSource: GetWaybillPagingSource,
    private val generalStorage: GeneralStorage,
    private val productsDao: ProductsDao
) : BaseRepository(), WaybillRepository {

    private val pagingConfig = PagingConfig(
        pageSize = 30,
        enablePlaceholders = false,
        initialLoadSize = 10
    )

    override suspend fun createWaybillProduct(
        params: CreateWaybillProductUseCase.CreateWaybillProductParams
    ): Either<Throwable, Unit> =
        apiCall {
            waybillApi.createWaybillProduct(
                CreateWaybillProductRequestDTO(
                    product = PartialWaybillProductDTO(
                        amount = params.amount,
                        barcode = params.barcode,
                        name = params.name,
                        purchasePrice = params.purchasePrice,
                        sellingPrice = params.sellingPrice,
                        waybillId = params.waybillId
                    )
                )
            )
        }

    override suspend fun getWaybillProductByBarcode(
        params: GetWaybillProductByBarcodeUseCase.GetWaybillProductByBarcodeParams
    ): Either<Throwable, WaybillProduct> = apiCall {
        waybillApi.getWaybillProductByBarcode(
            GetWaybillByBarcodeRequestDTO(
                barcode = params.barcode,
                waybillId = params.waybillId
            )
        )
    }.map {
        if (it.found) {
            withContext(coroutineContextProvider.io) {
                it.product.toDomain()
            }
        } else {
            return Either.Left(IllegalArgumentException("not found"))
        }
    }

    override suspend fun getProductByBarcode(
        params: GetProductByBarcodeUseCase.GetProductByBarcodeParams
    ): Either<Throwable, ProductEntity> =
        withContext(coroutineContextProvider.io) {
            val product = productsDao.getProductByBarcode(barcode = params.barcode)
            if (product == null) {
                return@withContext Either.Left(IllegalArgumentException("not found"))
            } else {
                return@withContext Either.Right(product)
            }
        }

    override suspend fun updateWaybillProduct(
        params: UpdateWaybillProductUseCase.UpdateWaybillProductParams
    ): Either<Throwable, Unit> = apiCall {
        waybillApi.updateWaybillProduct(
            CreateWaybillProductRequestDTO(
                product = PartialWaybillProductDTO(
                    amount = params.amount,
                    barcode = params.barcode,
                    name = params.name,
                    purchasePrice = params.purchasePrice,
                    sellingPrice = params.sellingPrice,
                    waybillId = params.waybillId,
                    id = params.productId
                )
            )
        )
    }

    override suspend fun conductWaybill(waybillId: Int): Either<Throwable, Waybill> =
        apiCall {
            waybillApi.conductWaybill(waybillId)
        }.map {
            withContext(coroutineContextProvider.io) {
                it.waybill.toDomain()
            }
        }

    override suspend fun rollbackWaybill(waybillId: Int): Either<Throwable, Waybill> =
        apiCall {
            waybillApi.rollbackWaybill(waybillId)
        }.map {
            withContext(coroutineContextProvider.io) {
                it.waybill.toDomain()
            }
        }

    override fun getAcceptanceListPaging(): Flow<PagingData<Waybill>> =
        Pager(
            config = pagingConfig
        ) {
            getWaybillPagingSource
        }.flow
            .map {
                it.map { waybillsDTO ->
                    waybillsDTO.toDomain()
                }
            }

    override fun getProducts(
        waybillId: Int
    ): Flow<PagingData<WaybillProduct>> =
        Pager(
            config = pagingConfig
        ) {
            GetWaybillProductsPagingSource(
                coroutineContextProvider = coroutineContextProvider,
                generalStorage = generalStorage,
                waybillApi = waybillApi,
                waybillId = waybillId
            )
        }.flow
            .map {
                it.map { productsDTO ->
                    productsDTO.toDomain()
                }
            }

    override suspend fun createDraftWaybill(): Either<Throwable, Waybill> =
        apiCall {
            val responseId = waybillApi.createWaybill(
                CreateWaybillRequestDTO(
                    waybill = PartialWaybill(
                        merchantId = generalStorage.getMerchantId(),
                        status = WaybillStatus.DRAFT.value,
                        stockId = generalStorage.getStockId(),
                        type = WaybillType.INWAYBILL.value
                    )
                )
            )

            waybillApi.getWaybillById(responseId.id)
        }.map {
            withContext(coroutineContextProvider.io) {
                it.waybill.toDomain()
            }
        }

}