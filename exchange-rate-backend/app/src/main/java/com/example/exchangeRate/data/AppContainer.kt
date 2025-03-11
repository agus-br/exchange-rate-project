package com.example.exchangeRate.data

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.exchangeRate.network.ExchangeRateApiService
import com.example.exchangeRate.network.RetrofitClient
import com.example.exchangeRate.workers.SyncWorker
import com.example.exchangeRate.workers.SyncWorkerFactory
import java.util.concurrent.TimeUnit

class AppContainer(
    private val context: Context
) {

    // Base de datos
    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    // DAO
    val exchangeRateDao: ExchangeRateDao by lazy {
        appDatabase.exchangeRateDao()
    }

    // Retrofit
    private val retrofit: ExchangeRateApiService by lazy {
        RetrofitClient.instance
    }

    // Repositorio
    val exchangeRateRepository: ExchangeRateRepository by lazy {
        ExchangeRateRepository(exchangeRateDao, retrofit)
    }

    // Worker que sincroniza los datos
    init {
        // Configura WorkManager con la fábrica personalizada
        val configuration = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory(exchangeRateRepository))
            .build()
        WorkManager.initialize(context, configuration)
    }

    val workManager: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Solo ejecutar si hay conexión a Internet
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES // Ejecutar cada 15 minutos
        ).setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "SyncWork", // Nombre único para el trabajo
            ExistingPeriodicWorkPolicy.KEEP, // La mantiene si existe
            syncRequest
        )
        Log.d("WorkManager", "Sincronización programada cada 15 minutos")
    }
}