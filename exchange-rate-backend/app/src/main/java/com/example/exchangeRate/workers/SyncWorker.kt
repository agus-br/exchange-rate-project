package com.example.exchangeRate.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.exchangeRate.data.ExchangeRateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ExchangeRateRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sincroniza los datos con la API
            repository.syncExchangeRates()
            Result.success()
        } catch (e: Exception) {
            // Si hay un error, reintenta más tarde
            Log.d("SyncWorker", "Fallo en la sincronización de datos con la API")
            Result.retry()
        }
    }
}