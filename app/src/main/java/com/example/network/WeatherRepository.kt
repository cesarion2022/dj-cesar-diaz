package com.example.network

import com.example.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WeatherRepository {

    private val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=4.4389&longitude=-75.2322&current=temperature_2m,weather_code&timezone=America%2FBogota"

    suspend fun fetchIbagueWeather(): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val url = URL(apiUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 7000
                readTimeout = 7000
                setRequestProperty("User-Agent", "DJSalsaFlow-Android")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                connection.disconnect()

                val root = JSONObject(response)
                val current = root.getJSONObject("current")
                val tempDouble = current.getDouble("temperature_2m")
                val tempInt = Math.round(tempDouble).toInt()
                val code = current.getInt("weather_code")

                val (icon, desc) = getWeatherDescription(code)
                WeatherInfo(
                    temp = tempInt,
                    description = desc,
                    iconEmoji = icon,
                    location = "Ibagué, Tolima",
                    isAvailable = true
                )
            } else {
                fallbackWeather()
            }
        } catch (e: Exception) {
            fallbackWeather()
        }
    }

    private fun fallbackWeather(): WeatherInfo {
        return WeatherInfo(
            temp = 24,
            description = "Mayormente despejado",
            iconEmoji = "🌤️",
            location = "Ibagué, Tolima",
            isAvailable = true
        )
    }

    private fun getWeatherDescription(code: Int): Pair<String, String> {
        return when (code) {
            0 -> "☀️" to "Despejado"
            1 -> "🌤️" to "Mayormente despejado"
            2 -> "⛅" to "Parcialmente nublado"
            3 -> "☁️" to "Nublado"
            45, 48 -> "🌫️" to "Niebla"
            51, 53 -> "🌦️" to "Llovizna"
            55 -> "🌧️" to "Llovizna fuerte"
            61 -> "🌧️" to "Lluvia ligera"
            63, 65 -> "🌧️" to "Lluvia"
            80, 81 -> "🌦️" to "Chubascos"
            82 -> "⛈️" to "Chubascos fuertes"
            95, 96, 99 -> "⛈️" to "Tormenta"
            else -> "🌤️" to "Agradable"
        }
    }
}
