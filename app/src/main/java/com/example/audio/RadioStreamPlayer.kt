package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RadioStreamPlayer(private val context: Context) {

    private val TAG = "RadioStreamPlayer"

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var currentUrl: String? = null

    fun playStream(url: String) {
        _errorMessage.value = null
        if (currentUrl == url && mediaPlayer != null) {
            if (!_isPlaying.value) {
                try {
                    mediaPlayer?.start()
                    _isPlaying.value = true
                } catch (e: Exception) {
                    Log.e(TAG, "Error resuming stream", e)
                    restartStream(url)
                }
            }
            return
        }

        restartStream(url)
    }

    private fun restartStream(url: String) {
        currentUrl = url
        _isBuffering.value = true
        _isPlaying.value = false

        releasePlayer()

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setVolume(_volume.value, _volume.value)

                setOnPreparedListener { mp ->
                    _isBuffering.value = false
                    _isPlaying.value = true
                    try {
                        mp.start()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to start after prepare", e)
                    }
                }

                setOnBufferingUpdateListener { _, percent ->
                    if (percent in 1..99 && !_isPlaying.value) {
                        _isBuffering.value = true
                    }
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    _isBuffering.value = false
                    _isPlaying.value = false
                    _errorMessage.value = "Reconectando transmisión en vivo..."
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MediaPlayer", e)
            _isBuffering.value = false
            _isPlaying.value = false
            _errorMessage.value = "No se pudo conectar a la estación"
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing", e)
        }
        _isPlaying.value = false
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            currentUrl?.let { playStream(it) }
        }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting volume", e)
        }
    }

    fun release() {
        releasePlayer()
        _isPlaying.value = false
        _isBuffering.value = false
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
        }
        mediaPlayer = null
    }
}
