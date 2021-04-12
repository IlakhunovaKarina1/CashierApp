package com.grappim.cashier

import android.app.Application
import com.grappim.cashier.core.platform.FocusedActivityHolder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CashierApp : Application() {

    @Inject
    lateinit var focusedActivityHolder: FocusedActivityHolder

    override fun onCreate() {
        super.onCreate()
        focusedActivityHolder.startListen(this)
    }
}