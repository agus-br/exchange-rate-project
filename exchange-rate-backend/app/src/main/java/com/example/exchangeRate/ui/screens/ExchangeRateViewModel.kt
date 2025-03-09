package com.example.exchangeRate.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.exchangeRate.ExchangeRateApplication
import com.example.exchangeRate.data.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateViewModel(
    private val repository: ExchangeRateRepository
) : ViewModel() {


    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Estado para las tasas de conversión
    private val _conversionRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val conversionRates: StateFlow<Map<String, Double>> = _conversionRates

    // Estado para el indicador de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para el mensaje de error
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    init {
        // Observar los cambios en las tasas de conversión
        observeConversionRates()
    }

    // Observar las tasas de conversión desde el repositorio
    private fun observeConversionRates() {
        viewModelScope.launch {
            repository.conversionRates.collect { rates ->
                // Log para verificar los datos recuperados de la base de datos
                Log.d("ExchangeRateViewModel", "Datos recuperados de la base de datos: $rates")
                _conversionRates.value = rates ?: emptyMap()
            }
        }
    }

    // Cargar los datos de la API
    fun loadExchangeRates() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                //repository.syncExchangeRates()
                // Log para verificar que la carga se completó correctamente
                Log.d("ExchangeRateViewModel", "Carga de datos completada")
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los datos: ${e.message}"
                // Log en caso de error al cargar datos
                Log.e("ExchangeRateViewModel", "Error en loadExchangeRates: ${e.message}")
            } finally {
                _isLoading.value = false
                // Log para verificar que la carga terminó (éxito o error)
                Log.d("ExchangeRateViewModel", "Carga de datos finalizada")
            }
        }
    }

    // Función de prueba para verificar la consulta del rango de fechas
    fun testDateRangeQuery(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            try {
                val startFormatted = dateFormat.format(Date(startDate))
                val endFormatted = dateFormat.format(Date(endDate))

                Log.d("ExchangeRateViewModel", "Fechas: $startFormatted - $endFormatted")
                // Ejecutar la consulta del rango de fechas
                val exchangeRates = repository.getExchangeRatesByDateRange(startDate, endDate).first()

                // Imprimir los resultados en el log
                Log.d("ExchangeRateViewModel", "Resultados de la consulta del rango de fechas:")
                exchangeRates.forEach { exchangeRate ->
                    Log.d("ExchangeRateViewModel", "ExchangeRate: $exchangeRate")
                }

                // Verificar si no se devolvieron datos
                if (exchangeRates.isEmpty()) {
                    Log.d("ExchangeRateViewModel", "No se encontraron datos en el rango de fechas especificado.")
                }
            } catch (e: Exception) {
                // Log en caso de error
                Log.e("ExchangeRateViewModel", "Error en testDateRangeQuery: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ExchangeRateApplication)
                val repository = application.container.exchangeRateRepository
                ExchangeRateViewModel(repository = repository)
            }
        }
    }
}