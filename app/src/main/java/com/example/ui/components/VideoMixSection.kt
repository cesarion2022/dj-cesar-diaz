package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.VideoMix
import com.example.ui.theme.NightBackground
import com.example.ui.theme.NightBorder
import com.example.ui.theme.NightBorderSubtle
import com.example.ui.theme.NightSurface
import com.example.ui.theme.NightSurfaceElevated
import com.example.ui.theme.NightSurfaceHover
import com.example.ui.theme.SalsaCyan
import com.example.ui.theme.SalsaGold
import com.example.ui.theme.SalsaPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private const val TAG = "VideoMixSection"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoMixSection(
    videoMixes: List<VideoMix>,
    selectedVideo: VideoMix,
    onSelectVideo: (VideoMix) -> Unit,
    modifier: Modifier = Modifier,
    onStartVideoPlayback: () -> Unit = {}
) {
    val context = LocalContext.current
    var isInlinePlayerActive by rememberSaveable { mutableStateOf(false) }
    var isRendererCrashed by remember { mutableStateOf(false) }

    fun openInYouTube() {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=${selectedVideo.youtubeId}")
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening YouTube", e)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VIDEOS EXCLUSIVOS",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Sesiones y mezclas en vivo por DJ César Díaz",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            // Quick open in YouTube App
            ElevatedButton(
                onClick = { openInYouTube() },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = NightSurfaceElevated,
                    contentColor = SalsaPink
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_youtube_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("YouTube", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Main Video Display Area (Player or Interactive Poster)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .border(1.dp, NightBorder, RoundedCornerShape(20.dp))
                .testTag("main_video_player")
        ) {
            if (isInlinePlayerActive && !isRendererCrashed) {
                // Safe Embedded WebView with Software Layer and RenderProcessGone handler
                AndroidView(
                    modifier = Modifier.matchParentSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            // CRITICAL: Disable hardware layer on WebView to prevent "Failed to open rendernode"
                            // and chromium GPU process termination (code -1) in emulator / virtualized devices
                            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                            setBackgroundColor(android.graphics.Color.BLACK)

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                mediaPlaybackRequiresUserGesture = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                            }

                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onRenderProcessGone(
                                    view: WebView?,
                                    detail: RenderProcessGoneDetail?
                                ): Boolean {
                                    // CRITICAL: Returning true tells the OS that the app handled the crash
                                    // preventing the OS from terminating the entire host application!
                                    Log.w(TAG, "WebView renderer process exited. Switching gracefully to fallback.")
                                    isRendererCrashed = true
                                    isInlinePlayerActive = false
                                    return true
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    Log.w(TAG, "WebView error: ${error?.description}")
                                }
                            }

                            val html = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                <style>
                                  body, html { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
                                  iframe { border:0; width:100%; height:100%; }
                                </style>
                                </head>
                                <body>
                                  <iframe src="https://www.youtube-nocookie.com/embed/${selectedVideo.youtubeId}?autoplay=1&playsinline=1&rel=0&modestbranding=1"
                                          frameborder="0"
                                          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                          allowfullscreen>
                                  </iframe>
                                </body>
                                </html>
                            """.trimIndent()

                            loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
                        }
                    },
                    update = { webView ->
                        val targetHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                            <style>
                              body, html { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
                              iframe { border:0; width:100%; height:100%; }
                            </style>
                            </head>
                            <body>
                              <iframe src="https://www.youtube-nocookie.com/embed/${selectedVideo.youtubeId}?autoplay=1&playsinline=1&rel=0&modestbranding=1"
                                      frameborder="0"
                                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                      allowfullscreen>
                              </iframe>
                            </body>
                            </html>
                        """.trimIndent()
                        webView.loadDataWithBaseURL("https://www.youtube.com", targetHtml, "text/html", "UTF-8", null)
                    },
                    onRelease = { webView ->
                        try {
                            webView.stopLoading()
                            webView.loadUrl("about:blank")
                            webView.clearHistory()
                            webView.removeAllViews()
                            webView.destroy()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error cleaning up WebView", e)
                        }
                    }
                )

                // Close Player button overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .clickable {
                                isInlinePlayerActive = false
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar video",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Cerrar reproductor",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .clickable { openInYouTube() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Abrir en YouTube",
                                tint = SalsaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "YouTube App",
                                color = SalsaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Interactive Poster / Fallback Card
                Box(modifier = Modifier.fillMaxSize()) {
                    // High-resolution Thumbnail
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(selectedVideo.hqThumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = selectedVideo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Cinematic Gradient Overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Center Content
                    if (isRendererCrashed) {
                        // Resilient Fallback message when graphics rendernode is absent
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Salsa Mix Disponible en YouTube",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Para la mejor fluidez en este emulador, puedes abrir la mezcla directamente con sonido de alta definición.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { openInYouTube() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SalsaPink,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ver en YouTube", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        isRendererCrashed = false
                                        onStartVideoPlayback()
                                        isInlinePlayerActive = true
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SalsaGold
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reintentar", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        // Standard interactive poster
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Big Glowing Play Button
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .shadow(16.dp, CircleShape, ambientColor = SalsaPink, spotColor = SalsaPink)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(SalsaPink, SalsaGold)
                                        )
                                    )
                                    .clickable {
                                        onStartVideoPlayback()
                                        isInlinePlayerActive = true
                                    }
                                    .testTag("start_video_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Reproducir Video",
                                    tint = NightBackground,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = selectedVideo.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .clickable {
                                            onStartVideoPlayback()
                                            isInlinePlayerActive = true
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "▶ Reproducir en App",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .clickable { openInYouTube() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "↗ Abrir en YouTube",
                                        color = SalsaGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Top Salsa Mix Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SESIÓN DJ • EN PANTALLA",
                            color = SalsaCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Currently Selected Details Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(NightSurface)
                .border(1.dp, NightBorderSubtle, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedVideo.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${selectedVideo.artist} • Oficial Salsa Mix",
                        color = SalsaGold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isInlinePlayerActive) SalsaPink.copy(alpha = 0.25f)
                            else NightSurfaceElevated
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isInlinePlayerActive) "EN REPRODUCCIÓN" else "LISTA DE ESPERA",
                        color = if (isInlinePlayerActive) SalsaPink else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Video Catalog Carousel
        Text(
            text = "CATÁLOGO DE MEZCLAS (${videoMixes.size})",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            itemsIndexed(videoMixes) { index, mix ->
                val isCurrent = mix.youtubeId == selectedVideo.youtubeId
                Box(
                    modifier = Modifier
                        .width(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCurrent) NightSurfaceHover else NightSurface)
                        .border(
                            1.5.dp,
                            if (isCurrent) SalsaPink else NightBorderSubtle,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            isRendererCrashed = false
                            onSelectVideo(mix)
                        }
                        .padding(8.dp)
                        .testTag("video_mix_item_$index")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Thumbnail with Play overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(mix.thumbnailUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = mix.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )

                            // Play button overlay
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCurrent) SalsaPink.copy(alpha = 0.9f)
                                        else Color.Black.copy(alpha = 0.6f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isCurrent) NightBackground else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Mix Index Tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    color = SalsaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Title & Artist
                        Text(
                            text = "Salsa Mix #${index + 1}",
                            color = if (isCurrent) SalsaGold else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = mix.artist,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
