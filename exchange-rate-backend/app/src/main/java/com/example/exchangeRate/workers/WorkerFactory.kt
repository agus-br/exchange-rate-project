package com.example.exchangeRate.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.WorkerFactory
import com.example.exchangeRate.data.ExchangeRateRepository

class SyncWorkerFactory(
    private val repository: ExchangeRateRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return SyncWorker(appContext, workerParameters, repository)
    }
}