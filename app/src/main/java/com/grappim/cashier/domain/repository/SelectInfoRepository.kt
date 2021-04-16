package com.grappim.cashier.domain.repository

import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.domain.outlet.Stock

interface SelectInfoRepository {
    suspend fun saveCashier(cashier: Cashier)
    suspend fun saveStock(stock: Stock)

    suspend fun getStocks(): Either<Throwable, List<Stock>>
    suspend fun getCashiers(): Either<Throwable, List<Cashier>>
}