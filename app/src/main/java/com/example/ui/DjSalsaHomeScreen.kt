package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.BookingCard
import com.example.ui.components.PercussionSoundboard
import com.example.ui.components.RadioPlayerCard
import com.example.ui.components.VideoMixSection
import com.example.ui.components.WeatherClockHeader
import com.example.ui.theme.NightBackground
import com.example.ui.theme.NightBorder
import com.example.ui.theme.NightSurface
import com.example.ui.theme.NightSurfaceElevated
import com.example.ui.theme.SalsaCyan
import com.example.ui.theme.SalsaGold
import com.example.ui.theme.SalsaPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.viewmodel.DjViewModel

@Composable
fun DjSalsaHomeScreen(
    viewModel: DjViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectedStationIndex by viewModel.selectedStationIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()
    val colombiaTime by viewModel.colombiaTime.collectAsStateWithLifecycle()
    val colombiaDate by viewModel.colombiaDate.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()

    val bpm by viewModel.bpm.collectAsStateWithLifecycle()
    val bpmCategory by viewModel.bpmCategory.collectAsStateWithLifecycle()

    // Pulse animation for floating WhatsApp button
    val infiniteTransition = rememberInfiniteTransition(label = "WaPulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaPulseScale"
    )

    fun openWhatsApp(ctx: Context) {
        try {
            val message = "¡Hola DJ César Díaz! Te escribo desde la app oficial de Todo el Poder de la Salsa."
            val url = "https://wa.me/573165275176?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ctx.startActivity(intent)
        } catch (_: Exception) {}
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = NightBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openWhatsApp(context) },
                containerColor = WhatsAppGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp, end = 4.dp)
                    .scale(pulseScale)
                    .shadow(16.dp, CircleShape, ambientColor = WhatsAppGreen, spotColor = WhatsAppGreen)
                    .testTag("floating_whatsapp_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💬",
                        fontSize = 18.sp
                    )
                    Text(
                        text = "WhatsApp",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_scroll_view"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // TOP HEADER / BRAND BAR
            item(key = "header_brand") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DJ Brand Logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        listOf(SalsaPink, SalsaGold, SalsaCyan, SalsaPink)
                                    )
                                )
                                .padding(2.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(NightBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "DJ",
                                    color = SalsaGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "DJ CÉSAR DÍAZ",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TODO EL PODER DE LA SALSA",
                                color = SalsaPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    // Contact pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(WhatsAppGreen)
                            .clickable { openWhatsApp(context) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "WhatsApp",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // HERO STATEMENT
            item(key = "hero_banner") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF260D2E),
                                    Color(0xFF190D24),
                                    Color(0xFF0F1826)
                                )
                            )
                        )
                        .border(1.dp, NightBorder, RoundedCornerShape(22.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .border(1.dp, SalsaPink, RoundedCornerShape(999.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "EN VIVO • SALSA • SABOR",
                                color = SalsaPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }

                        Text(
                            text = "TODO EL PODER\nDE LA SALSA",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Bienvenido a la plataforma oficial de DJ César Díaz. Radio en vivo, videos exclusivos y la mejor salsa las 24 horas desde Ibagué, Colombia.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // LIVE COLOMBIA CLOCK & IBAGUÉ WEATHER
            item(key = "weather_clock") {
                WeatherClockHeader(
                    currentTime = colombiaTime,
                    currentDate = colombiaDate,
                    weather = weather
                )
            }

            // LIVE RADIO PLAYER
            item(key = "radio_player") {
                RadioPlayerCard(
                    stations = viewModel.stations,
                    selectedIndex = selectedStationIndex,
                    isPlaying = isPlaying,
                    isBuffering = isBuffering,
                    volume = volume,
                    errorMessage = errorMessage,
                    onSelectStation = { viewModel.selectStation(it) },
                    onTogglePlayPause = { viewModel.togglePlayStation() },
                    onVolumeChange = { viewModel.setVolume(it) }
                )
            }

            // DJ CABIN: SOUNDBOARD & SALSA BPM METRONOME
            item(key = "dj_soundboard") {
                PercussionSoundboard(
                    onPlayEffect = { viewModel.playSoundEffect(it) },
                    bpm = bpm,
                    bpmCategory = bpmCategory,
                    onTapBpm = { viewModel.tapBpm() },
                    onResetBpm = { viewModel.resetBpm() }
                )
            }

            // EXCLUSIVE VIDEO MIXES (WEBVIEW EMBED + CAROUSEL)
            item(key = "video_mixes") {
                VideoMixSection(
                    videoMixes = viewModel.videoMixes,
                    selectedVideo = selectedVideo,
                    onSelectVideo = { viewModel.selectVideo(it) }
                )
            }

            // FIESTA & SALSA FEATURED BANNER
            item(key = "featured_salsa_banner") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "FIESTA & SALSA",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(NightSurface)
                            .border(1.dp, NightBorder, RoundedCornerShape(20.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("https://196448ee31.cbaul-cdnwnd.com/c010f3af7f90ba773e2f59374dc20797/200000053-726997269b/80-s%20music%20party%20%283%29.webp?ph=196448ee31")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Fiesta de salsa DJ César Díaz",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        // Gradient overlay with quote
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            NightBackground.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text(
                                    text = "RUMBA & SABOR EN VIVO",
                                    color = SalsaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "¡Siente la energía de la salsa en cada rincón!",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // CONTRATACIONES / BOOKING
            item(key = "booking_section") {
                BookingCard()
            }

            // FOOTER
            item(key = "footer") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "© 2026 DJ César Díaz — Todo el Poder de la Salsa",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ibagué, Tolima • Colombia",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Contacto: +57 316 527 5176",
                        color = SalsaGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
