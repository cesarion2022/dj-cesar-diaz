package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.SoundEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SalsaSynthesizer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playSound(effect: SoundEffect) {
        scope.launch {
            val pcmData = when (effect) {
                SoundEffect.CLAVE -> generateClave()
                SoundEffect.CAMPANA -> generateCampana()
                SoundEffect.GUIRO -> generateGuiro()
                SoundEffect.AIR_HORN -> generateAirHorn()
                SoundEffect.CONGA -> generateConga()
                SoundEffect.AZUCAR -> generateAzucarChime()
            }
            playPcm(pcmData)
        }
    }

    private fun playPcm(data: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(data.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(data, 0, data.size)
            audioTrack.play()
            // Track cleans up after completion
            scope.launch {
                val durationMs = (data.size * 1000L) / sampleRate + 200
                kotlinx.coroutines.delay(durationMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    // Classic crisp high-pitched Cuban clave click
    private fun generateClave(): ShortArray {
        val durationSeconds = 0.08
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val freq = 2400.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 80.0)
            val sample = sin(2.0 * PI * freq * t) * envelope
            buffer[i] = (sample * 0.9 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    // Latin Campana / Bongo Cowbell with dual metallic overtone
    private fun generateCampana(): ShortArray {
        val durationSeconds = 0.22
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val f1 = 587.0
        val f2 = 845.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 22.0)
            val sample = (0.6 * sin(2.0 * PI * f1 * t) + 0.4 * sin(2.0 * PI * f2 * t)) * env
            buffer[i] = (sample * 0.9 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    // Güiro rasp: rapid succession of short noise bursts (scratch rhythm)
    private fun generateGuiro(): ShortArray {
        val durationSeconds = 0.25
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val burstEnv = sin(2.0 * PI * 24.0 * t).let { if (it > 0) it else 0.0 }
            val noise = (Math.random() * 2.0 - 1.0)
            val decay = exp(-t * 6.0)
            val sample = noise * burstEnv * decay
            buffer[i] = (sample * 0.7 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    // DJ Reggaeton/Salsa Air Horn fanfare
    private fun generateAirHorn(): ShortArray {
        val durationSeconds = 0.45
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val f1 = 466.16 // Bb4
        val f2 = 554.37 // Db5

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = if (t < 0.02) t / 0.02 else exp(-(t - 0.02) * 4.0)
            // Square-ish / brassy harmonics
            val s1 = sin(2.0 * PI * f1 * t) + 0.3 * sin(4.0 * PI * f1 * t) + 0.15 * sin(6.0 * PI * f1 * t)
            val s2 = sin(2.0 * PI * f2 * t) + 0.3 * sin(4.0 * PI * f2 * t)
            val sample = (s1 * 0.6 + s2 * 0.4) * env
            buffer[i] = (sample * 0.8 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    // Conga slap body
    private fun generateConga(): ShortArray {
        val durationSeconds = 0.18
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val startFreq = 340.0
        val endFreq = 180.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val currentFreq = startFreq + (endFreq - startFreq) * (t / durationSeconds)
            val env = exp(-t * 26.0)
            val sample = sin(2.0 * PI * currentFreq * t) * env
            buffer[i] = (sample * 0.9 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    // Azúcar celebratory trumpet / Latin flourish
    private fun generateAzucarChime(): ShortArray {
        val durationSeconds = 0.35
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val f1 = 523.25 // C5
        val f2 = 659.25 // E5
        val f3 = 783.99 // G5

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 8.0)
            val sample = (0.4 * sin(2.0 * PI * f1 * t) + 0.3 * sin(2.0 * PI * f2 * t) + 0.3 * sin(2.0 * PI * f3 * t)) * env
            buffer[i] = (sample * 0.85 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
