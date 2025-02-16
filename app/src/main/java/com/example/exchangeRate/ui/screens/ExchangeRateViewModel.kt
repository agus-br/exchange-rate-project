package com.example.exchangeRate.ui.screens

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
import kotlinx.coroutines.launch

class ExchangeRateViewModel(
    private val repository: ExchangeRateRepository
) : ViewModel() {

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
                repository.syncExchangeRates()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los datos: ${e.message}"
                println("Error en loadExchangeRates: ${e.message}") // Imprimir el error en la consola
            } finally {
                _isLoading.value = false
                println("Carga de datos completada") // Imprimir cuando la carga termine
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