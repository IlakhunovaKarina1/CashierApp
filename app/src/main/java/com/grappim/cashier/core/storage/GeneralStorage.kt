package com.grappim.cashier.core.storage

import android.content.Context
import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.domain.outlet.Outlet
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val STORAGE_NAME = "cashier_data_store"

        private const val CASHIER_NAME = "cashier_name"
        private const val CASHIER_ID = "cashier_id"

        private const val OUTLET_NAME = "outlet_name"

        private const val MERCHANT_ID = "merchant_id"
        private const val AUTH_TOKEN = "auth_token"
    }

    private val sharedPreferences = context
        .getSharedPreferences(
            STORAGE_NAME,
            Context.MODE_PRIVATE
        )

    private val editor = sharedPreferences.edit()

    fun setCashierInfo(cashier: Cashier) {
        editor
            .putString(CASHIER_ID, cashier.id)
            .putString(CASHIER_NAME, cashier.name)
            .apply()
    }

    fun setOutletInfo(outlet: Outlet) {
        editor
            .putString(OUTLET_NAME, outlet.name)
            .apply()
    }

    fun setAuthToken(token: String) {
        editor
            .putString(AUTH_TOKEN, token)
            .apply()
    }

    fun setMerchantId(merchantId: String) {
        editor
            .putString(MERCHANT_ID, merchantId)
            .apply()
    }

    fun getCashierName(): String = getStringValue(CASHIER_NAME)

    fun getCashierId(): String = getStringValue(CASHIER_ID)

    fun getOutletName(): String = getStringValue(OUTLET_NAME)

    fun getMerchantId(): String = getStringValue(MERCHANT_ID)

    fun getBearerAuthToken(): String =
        "Bearer ${getStringValue(AUTH_TOKEN)}"

    fun clearData(){
        editor.clear().apply()
    }

    private fun getStringValue(key: String): String =
        sharedPreferences.getString(key, null)
            ?: throw IllegalArgumentException("no value for $key")


}