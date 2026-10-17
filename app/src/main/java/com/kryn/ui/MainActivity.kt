package com.kryn.ui

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KrynStorefrontApp()
        }
    }
}

// Modern Native Android Typefaces
val CondensedDisplayFont = FontFamily(Typeface.create("sans-serif-condensed", Typeface.BOLD))
val MonoBrandFont = FontFamily.Monospace
val CleanSansFont = FontFamily(Typeface.create("sans-serif-medium", Typeface.NORMAL))

data class CategoryItem(val title: String, val subtitle: String, val imageUrl: String)
data class WidgetItem(val name: String, val tag: String, val author: String, val imageUrl: String)

@Composable
fun KrynStorefrontApp() {
    val categories = remember {
        listOf(
            CategoryItem("ANIME", "INK & CHERRY", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&q=80"),
            CategoryItem("MINIMAL", "SLATE & MONO", "https://images.unsplash.com/photo-1507499739999-097706ad8914?w=600&q=80"),
            CategoryItem("CYBER", "RACE TELEMETRY", "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&q=80"),
            CategoryItem("MONOLITH", "BRUTALIST ARCH", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&q=80")
        )
    }

    val widgetShowcase = remember {
        listOf(
            WidgetItem("KRYN // 01", "TELEMETRY CLOCK", "ENGINE 1.0", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
            WidgetItem("KRYN // 02", "BATTERY GAUGE", "VOLT DYNAMICS", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
            WidgetItem("KRYN // 03", "MINIMALIST MONOLITH", "PURE TYPO", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
            WidgetItem("KRYN // 04", "MEDIA DOCK", "AUDIO MATRIX", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
            WidgetItem("KRYN // 05", "HORIZON WEATHER", "RADAR HUD", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
            WidgetItem("KRYN // 06", "JAPAN GP // SEKTOR", "SUZUKA TRIBUTE", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80")
        )
    }

    Scaffold(
        containerColor = Color(0xFFF6F7FA) // Clean ceramic light base
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 44.dp, bottom = 48.dp)
        ) {
            // 1. RE-ARCHITECTED BRAND HEADER (Dominant Hierarchy)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // High-Impact Brand Headline
                            Text(
                                text = "KRYN UI",
                                color = Color(0xFF0F1117),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.5.sp,
                                fontFamily = CondensedDisplayFont
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Pulsing live status dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE10600))
                            )
                        }
                        Text(
                            text = "BY KRYN JOINT LABS // ENGINE v1.0",
                            color = Color(0xFFE10600),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp,
                            fontFamily = MonoBrandFont
                        )
                    }

                    // Version Badge Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "01 // DEV",
                            color = Color(0xFF0F1117),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MonoBrandFont
                        )
                    }
                }
            }

            // 2. HERO CARD (Japanese Scenery Artwork + Gradient Mask)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .height(260.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(26.dp))
                ) {
                    // Japanese Scenery Background (Fushimi Inari Torii / Ink Forest)
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=1200&q=85",
                        contentDescription = "Japanese Scenery",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Gradient Overlay (Dark scrim for high-contrast white text)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x33000000),
                                        Color(0x990A0B10),
                                        Color(0xF2090A0F)
                                    )
                                )
                            )
                    )

                    // Overlay Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(22.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE10600))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DESKTOP PRECISION // ON MOBILE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontFamily = MonoBrandFont
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "APP UNDER CONSTRUCTION",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontFamily = CondensedDisplayFont
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Engineered for creators, racers, and minimalists.",
                            color = Color(0xFFD1D5DB),
                            fontSize = 12.sp,
                            fontFamily = CleanSansFont
                        )
                    }
                }
            }

            // 3. CATEGORIES SECTION (Horizontal Gallery)
            item {
                Column(modifier = Modifier.padding(top = 26.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPLORE BY STYLE",
                            color = Color(0xFF0F1117),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = CondensedDisplayFont
                        )
                        Text(
                            text = "ALL CATEGORIES →",
                            color = Color(0xFF6B7280),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MonoBrandFont
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(categories) { index, item ->
                            AnimatedCategoryCard(index = index, item = item)
                        }
                    }
                }
            }

            // 4. SIGNATURE DROPS SECTION HEADER
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIGNATURE DROPS",
                        color = Color(0xFF0F1117),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = CondensedDisplayFont
                    )
                    Text(
                        text = "v1.0 ACTIVE",
                        color = Color(0xFFE10600),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonoBrandFont
                    )
                }
            }

            // 5. WIDGET GRID (Animated on entrance)
            itemsIndexed(widgetShowcase.chunked(2)) { rowIndex, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEachIndexed { colIndex, widget ->
                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedWidgetCard(
                                index = rowIndex * 2 + colIndex,
                                widget = widget
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 6. BRAND FOOTER
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 44.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(2.dp)
                            .background(Color(0xFFE2E4E9))
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Made by Eveleske",
                        color = Color(0xFF0F1117),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = MonoBrandFont
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "From the Community to the Community",
                        color = Color(0xFF6B7280),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        fontFamily = MonoBrandFont
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedCategoryCard(index: Int, item: CategoryItem) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = index * 80,
                easing = FastOutSlowInEasing
            )
        ) + slideInHorizontally(
            initialOffsetX = { 60 },
            animationSpec = tween(durationMillis = 400, delayMillis = index * 80)
        )
    ) {
        Box(
            modifier = Modifier
                .width(170.dp)
                .height(115.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(20.dp))
                .clickable { }
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x15000000), Color(0xDD090A0F))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("→", color = Color.White, fontSize = 12.sp)
                }
                Column {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CondensedDisplayFont
                    )
                    Text(
                        text = item.subtitle,
                        color = Color(0xFFD1D5DB),
                        fontSize = 9.sp,
                        fontFamily = MonoBrandFont
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedWidgetCard(index: Int, widget: WidgetItem) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = (index * 60).coerceAtMost(300),
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = (index * 60).coerceAtMost(300)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(20.dp))
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AsyncImage(
                    model = widget.imageUrl,
                    contentDescription = widget.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(Color(0xE6FFFFFF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = widget.tag,
                        color = Color(0xFF0F1117),
                        fontSize = 8.sp,
                        fontFamily = MonoBrandFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = widget.name,
                color = Color(0xFF0F1117),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CondensedDisplayFont
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = widget.author,
                    color = Color(0xFF6B7280),
                    fontSize = 9.sp,
                    fontFamily = MonoBrandFont
                )
                Text(
                    text = "APPLY",
                    color = Color(0xFFE10600),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonoBrandFont
                )
            }
        }
    }
}
