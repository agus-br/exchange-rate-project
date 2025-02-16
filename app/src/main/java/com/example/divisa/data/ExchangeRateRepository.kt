package com.example.divisa.data

import ExchangeRate
import ExchangeRateDao
import com.example.divisa.network.ExchangeRateApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExchangeRateRepository(
    private val exchangeRateDao: ExchangeRateDao,
    private val apiService: ExchangeRateApiService
) {

    // Obtén el último tipo de cambio como un Flow
    val latestExchangeRate: Flow<ExchangeRate?> = exchangeRateDao.getLatestExchangeRate()

    // Inserta un nuevo tipo de cambio
    suspend fun insert(exchangeRate: ExchangeRate) {
        exchangeRateDao.insert(exchangeRate)
    }

    // Obtén las tasas de conversión como un Flow
    val conversionRates: Flow<Map<String, Double>?> = latestExchangeRate.map { exchangeRate ->
        exchangeRate?.rates
    }

    // Sincroniza los datos con la API
    suspend fun syncExchangeRates() {
        try {
            val response = apiService.getLatestExchangeRates()
            if (response.result == "success") {
                val exchangeRate = ExchangeRate(
                    id = 0,
                    baseCode = response.base_code,
                    rates = response.conversion_rates,
                    lastUpdateUnix = response.time_last_update_unix,
                    nextUpdateUnix = response.time_next_update_unix
                )
                insert(exchangeRate)
            }
        } catch (e: Exception) {
            println("Error al sincronizar los datos: ${e.message}")
        }
    }
}