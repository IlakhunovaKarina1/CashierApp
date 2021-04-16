package com.grappim.cashier.di.modules

import com.grappim.cashier.data.repository.GeneralRepository
import com.grappim.cashier.data.repository.GeneralRepositoryImpl
import com.grappim.cashier.data.repository.SelectInfoRepository
import com.grappim.cashier.data.repository.SelectInfoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGeneralRepository(
        generalRepositoryImpl: GeneralRepositoryImpl
    ): GeneralRepository

    @Binds
    abstract fun bindSelectInfoRepository(
        selectInfoRepositoryImpl: SelectInfoRepositoryImpl
    ): SelectInfoRepository
}