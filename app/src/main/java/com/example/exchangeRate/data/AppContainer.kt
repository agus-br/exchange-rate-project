package com.example.exchangeRate.data

import android.content.Context
import com.example.exchangeRate.network.ExchangeRateApiService
import com.example.exchangeRate.network.RetrofitClient

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
}