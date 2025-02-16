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

    // Obtén el último registro de la tabla como un Flow
    @Query("SELECT * FROM exchange_rates LIMIT 1")
    fun getLatestExchangeRate(): Flow<ExchangeRate?>

    // Elimina todos los registros de la tabla (útil para limpiar la tabla)
    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAll()
}