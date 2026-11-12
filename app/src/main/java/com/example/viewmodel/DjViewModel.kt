package com.example.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.RadioStreamPlayer
import com.example.audio.SalsaSynthesizer
import com.example.model.RadioStation
import com.example.model.SoundEffect
import com.example.model.VideoMix
import com.example.model.WeatherInfo
import com.example.network.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DjViewModel(application: Application) : AndroidViewModel(application) {

    private val radioPlayer = RadioStreamPlayer(application.applicationContext)
    private val synthesizer = SalsaSynthesizer()
    private val weatherRepo = WeatherRepository()

    // Radio Stations
    val stations = listOf(
        RadioStation(1, "Salsa Poder 1", "https://stream.zeno.fm/qmqnw2t6v68uv", "Salsa brava, clásica y guaguancó"),
        RadioStation(2, "Salsa Poder 2", "https://stream.zeno.fm/xb7ex6ns9k0uv", "Salsa romántica y de alcoba"),
        RadioStation(3, "Salsa Poder 3", "https://stream.zeno.fm/9asu47reswzuv", "Salsa dura y descargas latinas")
    )

    private val _selectedStationIndex = MutableStateFlow(0)
    val selectedStationIndex: StateFlow<Int> = _selectedStationIndex.asStateFlow()

    val isPlaying: StateFlow<Boolean> = radioPlayer.isPlaying
    val isBuffering: StateFlow<Boolean> = radioPlayer.isBuffering
    val volume: StateFlow<Float> = radioPlayer.volume
    val errorMessage: StateFlow<String?> = radioPlayer.errorMessage

    // Video Mixes
    val videoMixes = listOf(
        VideoMix(1, "of6SQmMulCQ", "Salsa Mix #1 - Lo Mejor de la Salsa Brava"),
        VideoMix(2, "1ocFlSOJeiw", "Salsa Mix #2 - Clásicos Inmortales"),
        VideoMix(3, "9_MFJjenEjI", "Salsa Mix #3 - Azúcar y Candela"),
        VideoMix(4, "pVlOZlmwJhY", "Salsa Mix #4 - Pura Gozadera"),
        VideoMix(5, "rA5yWBADVpU", "Salsa Mix #5 - Golpe Callejero"),
        VideoMix(6, "fK0AfjfBdUk", "Salsa Mix #6 - Con Sabor a Ibagué"),
        VideoMix(7, "eR-JIsXzrmE", "Salsa Mix #7 - Descarga Tropical"),
        VideoMix(8, "Xy0h3643kpk", "Salsa Mix #8 - Tremendo Tumbao"),
        VideoMix(9, "2fFfyVu9CFY", "Salsa Mix #9 - Soneros del Barrio"),
        VideoMix(10, "SQqpOx_gXTk", "Salsa Mix #10 - Rumba y Percusión"),
        VideoMix(11, "FkpKdGwSVqc", "Salsa Mix #11 - Salsa Romántica Flow"),
        VideoMix(12, "t93w770FZo0", "Salsa Mix #12 - Fiesta y Vacilón"),
        VideoMix(13, "-_G3xNG7pZk", "Salsa Mix #13 - De Cali a Ibagué"),
        VideoMix(14, "KwMKN8zHOMo", "Salsa Mix #14 - Ritmo y Tambor"),
        VideoMix(15, "y_4r8BtPTlk", "Salsa Mix #15 - Rompe Tarima"),
        VideoMix(16, "EzAxuazHaAI", "Salsa Mix #16 - Noche Tropical"),
        VideoMix(17, "UbExOtljack", "Salsa Mix #17 - Fuego en la Pista"),
        VideoMix(18, "hpz_3nsYDJE", "Salsa Mix #18 - Sesión DJ de Oro")
    )

    private val _selectedVideo = MutableStateFlow(videoMixes[0])
    val selectedVideo: StateFlow<VideoMix> = _selectedVideo.asStateFlow()

    // Clock and Date (Bogota, Colombia)
    private val _colombiaTime = MutableStateFlow("--:--:--")
    val colombiaTime: StateFlow<String> = _colombiaTime.asStateFlow()

    private val _colombiaDate = MutableStateFlow("Cargando fecha...")
    val colombiaDate: StateFlow<String> = _colombiaDate.asStateFlow()

    // Weather
    private val _weather = MutableStateFlow(WeatherInfo())
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    // BPM Tap State
    private val tapTimestamps = mutableListOf<Long>()
    private val _bpm = MutableStateFlow<Int?>(null)
    val bpm: StateFlow<Int?> = _bpm.asStateFlow()

    private val _bpmCategory = MutableStateFlow<String?>(null)
    val bpmCategory: StateFlow<String?> = _bpmCategory.asStateFlow()

    private var clockJob: Job? = null

    init {
        startClock()
        loadWeather()
    }

    fun selectStation(index: Int) {
        if (index in stations.indices) {
            _selectedStationIndex.value = index
            val station = stations[index]
            if (isPlaying.value || isBuffering.value) {
                radioPlayer.playStream(station.url)
            }
        }
    }

    fun pauseRadio() {
        radioPlayer.pause()
    }

    fun togglePlayStation() {
        val currentStation = stations[_selectedStationIndex.value]
        if (isPlaying.value) {
            radioPlayer.pause()
        } else {
            radioPlayer.playStream(currentStation.url)
        }
    }

    fun setVolume(vol: Float) {
        radioPlayer.setVolume(vol)
    }

    fun selectVideo(video: VideoMix) {
        _selectedVideo.value = video
    }

    fun playSoundEffect(effect: SoundEffect) {
        synthesizer.playSound(effect)
    }

    fun tapBpm() {
        val now = SystemClock.uptimeMillis()
        // Reset if interval > 2.5s
        if (tapTimestamps.isNotEmpty() && (now - tapTimestamps.last()) > 2500) {
            tapTimestamps.clear()
        }

        tapTimestamps.add(now)
        if (tapTimestamps.size > 8) {
            tapTimestamps.removeAt(0)
        }

        if (tapTimestamps.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 1 until tapTimestamps.size) {
                intervals.add(tapTimestamps[i] - tapTimestamps[i - 1])
            }
            val avgInterval = intervals.average()
            if (avgInterval > 0) {
                val calculatedBpm = Math.round(60000.0 / avgInterval).toInt()
                _bpm.value = calculatedBpm
                _bpmCategory.value = when {
                    calculatedBpm < 85 -> "Tiempo Lento (Bolero / Cha-cha)"
                    calculatedBpm in 85..98 -> "Salsa Romántica (Suave)"
                    calculatedBpm in 99..108 -> "Salsa Clásica / Guaguancó"
                    calculatedBpm in 109..122 -> "Salsa Brava / Dura 🔥"
                    else -> "Timba / Descarga Rápida ⚡"
                }
            }
        }
    }

    fun resetBpm() {
        tapTimestamps.clear()
        _bpm.value = null
        _bpmCategory.value = null
    }

    private fun startClock() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            val tz = TimeZone.getTimeZone("America/Bogota")
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale("es", "CO")).apply { timeZone = tz }
            val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CO")).apply { timeZone = tz }

            while (isActive) {
                val now = Date()
                _colombiaTime.value = timeFormat.format(now)
                _colombiaDate.value = dateFormat.format(now).uppercase(Locale("es", "CO"))
                delay(1000)
            }
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            val info = weatherRepo.fetchIbagueWeather()
            _weather.value = info
        }
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
        radioPlayer.release()
    }
}
