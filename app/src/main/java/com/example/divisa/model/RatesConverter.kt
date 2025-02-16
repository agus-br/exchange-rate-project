import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RatesConverter {

    private val gson = Gson()

    // Convierte un Map<String, Double> a JSON (String)
    @TypeConverter
    fun fromRatesMap(rates: Map<String, Double>): String {
        return gson.toJson(rates)
    }

    // Convierte un JSON (String) a Map<String, Double>
    @TypeConverter
    fun toRatesMap(ratesJson: String): Map<String, Double> {
        val type = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(ratesJson, type)
    }
}