package com.grappim.cashier.data.repository

import com.grappim.cashier.api.CashierApi
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.core.functional.map
import com.grappim.cashier.core.storage.GeneralStorage
import com.grappim.cashier.data.remote.BaseRepository
import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.data.remote.model.outlet.OutletMapper.toDomain
import com.grappim.cashier.domain.outlet.Stock
import com.grappim.cashier.domain.repository.SelectInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectInfoRepositoryImpl @Inject constructor(
    private val cashierApi: CashierApi,
    private val generalStorage: GeneralStorage,
) : BaseRepository(), SelectInfoRepository {

    override suspend fun saveCashier(cashier: Cashier) = withContext(Dispatchers.IO) {
        generalStorage.setCashierInfo(cashier)
    }

    override suspend fun saveStock(stock: Stock) = withContext(Dispatchers.IO) {
        generalStorage.setStockInfo(stock)
    }

    override suspend fun getStocks(): Either<Throwable, List<Stock>> =
        apiCall {
            val merchantId = generalStorage.getMerchantId()
            cashierApi.getStocks(merchantId)
        }.map {
            withContext(Dispatchers.IO) {
                it.stocks.toDomain()
            }
        }

    override suspend fun getCashiers(): Either<Throwable, List<Cashier>> =
        Either.Right(getCashierList())

    private fun getCashierList(): List<Cashier> {
        val cashiers = mutableListOf<Cashier>()
        (0..10).forEach {
            cashiers.add(Cashier(id = "$it", name = "Cashier $it"))
        }
        return cashiers.toList()
    }

}