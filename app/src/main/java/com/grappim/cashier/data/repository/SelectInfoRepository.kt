package com.grappim.cashier.data.repository

import com.grappim.cashier.api.CashierApi
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.core.functional.map
import com.grappim.cashier.core.storage.GeneralStorage
import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.domain.outlet.Mapper.toDomain
import com.grappim.cashier.domain.outlet.Outlet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface SelectInfoRepository {
    suspend fun saveCashier(cashier: Cashier)
    suspend fun saveOutlet(outlet: Outlet)

    suspend fun getOutlets(): Either<Throwable, List<Outlet>>
    suspend fun getCashiers(): Either<Throwable, List<Cashier>>
}

@Singleton
class SelectInfoRepositoryImpl @Inject constructor(
    private val cashierApi: CashierApi,
    private val generalStorage: GeneralStorage,
) : BaseRepository(), SelectInfoRepository {

    override suspend fun saveCashier(cashier: Cashier) = withContext(Dispatchers.IO) {
        generalStorage.setCashierInfo(cashier)
    }

    override suspend fun saveOutlet(outlet: Outlet) = withContext(Dispatchers.IO) {
        generalStorage.setOutletInfo(outlet)
    }

    override suspend fun getOutlets(): Either<Throwable, List<Outlet>> =
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