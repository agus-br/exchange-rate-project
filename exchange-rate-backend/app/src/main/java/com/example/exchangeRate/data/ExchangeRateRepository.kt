package com.example.exchangeRate.data

import android.util.Log
import com.example.exchangeRate.model.ExchangeRate
import com.example.exchangeRate.network.ExchangeRateApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class ExchangeRateRepository(
    private val exchangeRateDao: ExchangeRateDao,
    private val apiService: ExchangeRateApiService
) {

    // Obtén el último tipo de cambio como un Flow
    val latestExchangeRate: Flow<ExchangeRate?> = exchangeRateDao.getLatestExchangeRate()

    // Obtén los tipos de cambio por rango de fechas
    fun getExchangeRatesByDateRange(startDate: Long, endDate: Long): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getExchangeRatesByDateRange(startDate, endDate)
    }

    // Inserta un nuevo tipo de cambio
    private suspend fun insert(exchangeRate: ExchangeRate) {
        try {
            exchangeRateDao.insert(exchangeRate)
            // Log para verificar que los datos se insertaron correctamente
            Log.d("ExchangeRateRepository", "Datos insertados en la base de datos: $exchangeRate")
        } catch (e: Exception) {
            // Log en caso de error al insertar
            Log.e("ExchangeRateRepository", "Error al insertar datos: ${e.message}")
        }
    }

    // Elimina todos los registros de la tabla
    suspend fun deleteAll() {
        try {
            exchangeRateDao.deleteAll()
            Log.d("ExchangeRateRepository", "Todos los registros eliminados.")
        } catch (e: Exception) {
            Log.e("ExchangeRateRepository", "Error al eliminar todos los registros: ${e.message}")
        }
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
                // Imprimir los datos de la API en la consola usando Log.d
                Log.d("ExchangeRateRepository", "Datos de la API recibidos: $response")

                val exchangeRate = ExchangeRate(
                    baseCode = response.base_code,
                    rates = response.conversion_rates,
                    lastUpdateUnix = response.time_last_update_unix,
                    nextUpdateUnix = response.time_next_update_unix,
                    syncDate = System.currentTimeMillis()
                )
                insert(exchangeRate)
            }
        } catch (e: Exception) {
            Log.e("ExchangeRateRepository", "Error al sincronizar los datos: ${e.message}")
        }
    }


}