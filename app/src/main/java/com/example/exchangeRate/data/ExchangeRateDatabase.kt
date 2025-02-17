package com.example.exchangeRate.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.exchangeRate.model.ExchangeRate
import com.example.exchangeRate.model.RatesConverter

@Database(
    entities = [ExchangeRate::class], // Entidades que forman parte de la base de datos
    version = 1, // Versión de la base de datos
    exportSchema = false // No necesitamos exportar el esquema
)
@TypeConverters(RatesConverter::class) // Registra el TypeConverter
abstract class AppDatabase : RoomDatabase() {

    abstract fun exchangeRateDao(): ExchangeRateDao // DAO para acceder a la tabla

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exchange_rate_database" // Nombre de la base de datos
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}