package com.example.model

data class RadioStation(
    val id: Int,
    val name: String,
    val url: String,
    val description: String = "Streaming 24/7 • Salsa brava, romántica y clásica"
)

data class VideoMix(
    val id: Int,
    val youtubeId: String,
    val title: String,
    val artist: String = "DJ César Díaz",
    val description: String = "Salsa con flow en vivo"
) {
    val thumbnailUrl: String
        get() = "https://img.youtube.com/vi/$youtubeId/mqdefault.jpg"

    val hqThumbnailUrl: String
        get() = "https://img.youtube.com/vi/$youtubeId/hqdefault.jpg"

    val embedUrl: String
        get() = "https://www.youtube.com/embed/$youtubeId?autoplay=1&playsinline=1&rel=0"
}

data class WeatherInfo(
    val temp: Int = 22,
    val description: String = "Cargando clima...",
    val iconEmoji: String = "🌤️",
    val location: String = "Ibagué, Tolima",
    val isAvailable: Boolean = true
)

enum class SoundEffect(val title: String, val emoji: String, val subtitle: String) {
    CLAVE("Clave 3-2", "🪘", "Madera"),
    CAMPANA("Campana", "🔔", "Cowbell"),
    GUIRO("Güiro", "🪇", "Raspado"),
    AIR_HORN("DJ Horn", "📢", "Drop"),
    AZUCAR("¡Azúcar!", "✨", "Grito"),
    CONGA("Conga Slap", "🥁", "Tumbao")
}
