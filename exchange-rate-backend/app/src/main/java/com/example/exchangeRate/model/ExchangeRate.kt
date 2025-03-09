package com.example.exchangeRate.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "exchange_rates") // Nombre de la tabla en la base de datos
data class ExchangeRate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Clave primaria autoincremental
    val baseCode: String, // Moneda base (por ejemplo, "USD")
    @TypeConverters(RatesConverter::class) // Convertidor para el mapa de tasas
    val rates: Map<String, Double>, // Tasas de conversión (por ejemplo, {"USD": 1.0, "EUR": 0.95})
    val lastUpdateUnix: Long, // Marca de tiempo de la última actualización
    val nextUpdateUnix: Long, // Marca de tiempo de la próxima actualización
    val syncDate: Long // Nueva marca de tiempo para la fecha de sincronización
)