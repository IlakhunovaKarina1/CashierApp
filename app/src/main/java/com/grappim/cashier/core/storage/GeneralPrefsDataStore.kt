package com.grappim.cashier.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grappim.cashier.core.domain.Cashier
import com.grappim.cashier.core.domain.Outlet
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralPrefsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val STORAGE_NAME = "cashier_data_store"

        private const val CASHIER_NAME = "cashier_name"
        private const val CASHIER_ID = "cashier_id"

        private const val OUTLET_NAME = "outlet_name"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = STORAGE_NAME
    )

    private val cashierNameKey: Preferences.Key<String> =
        stringPreferencesKey(CASHIER_NAME)

    private val cashierIdKey: Preferences.Key<String> =
        stringPreferencesKey(CASHIER_ID)

    private val outletNameKey: Preferences.Key<String> =
        stringPreferencesKey(OUTLET_NAME)

    val cashierName: Flow<String> = context.dataStore.data.map {
        it.getPreference(cashierNameKey)
    }

    val cashierId: Flow<String> = context.dataStore.data.map {
        it.getPreference(cashierIdKey)
    }

    val outletName: Flow<String> = context.dataStore.data.map {
        it.getPreference(outletNameKey)
    }

    suspend fun setOutletInfo(outlet: Outlet) {
        setOutletName(outlet.name)
    }

    suspend fun setCashierInfo(cashier: Cashier) {
        setCashierName(cashier.name)
        setCashierId(cashier.id)
    }

    private suspend fun setOutletName(value: String) = setValue(outletNameKey, value)

    private suspend fun setCashierName(value: String) = setValue(cashierNameKey, value)

    private suspend fun setCashierId(value: String) = setValue(cashierIdKey, value)

    private suspend fun setValue(key: Preferences.Key<String>, value: String) =
        withContext(Dispatchers.IO) {
            context.dataStore.edit {
                it[key] = value
            }
        }

    suspend fun clearDataStore() {
        context.dataStore.edit {
            it.clear()
        }
    }

    private fun <T> Preferences.getPreference(key: Preferences.Key<T>): T =
        this[key] ?: throw IllegalArgumentException("no value for ${key.name} in dataStore")
}