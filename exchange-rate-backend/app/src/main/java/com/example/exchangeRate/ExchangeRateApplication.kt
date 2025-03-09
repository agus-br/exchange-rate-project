package com.example.exchangeRate

import android.app.Application
import com.example.exchangeRate.data.AppContainer

class ExchangeRateApplication : Application() {

    // Contenedor de dependencias
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        // Programar la sincronización
        container.scheduleSync()
    }
}