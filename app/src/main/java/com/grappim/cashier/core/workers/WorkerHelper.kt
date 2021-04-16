package com.grappim.cashier.core.workers

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startTokenRefresherWorker() {
        startTokenRefresher(context)
    }
}