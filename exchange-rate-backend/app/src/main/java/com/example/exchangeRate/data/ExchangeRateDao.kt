package com.example.exchangeRate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.exchangeRate.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    // Inserta o actualiza un registro en la tabla
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exchangeRate: ExchangeRate)

    // Obtiene el último tipo de cambio basado en la fecha de sincronización más reciente
    @Query("SELECT * FROM exchange_rates ORDER BY syncDate DESC LIMIT 1")
    fun getLatestExchangeRate(): Flow<ExchangeRate?>

    // Obtiene todos los registros dentro de un rango de fechas específico
    @Query("SELECT * FROM exchange_rates WHERE syncDate BETWEEN :startDate AND :endDate ORDER BY lastUpdateUnix ASC")
    fun getExchangeRatesByDateRange(startDate: Long, endDate: Long): Flow<List<ExchangeRate>>

    // Elimina todos los registros de la tabla (útil para limpiar la tabla)
    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAll()

}