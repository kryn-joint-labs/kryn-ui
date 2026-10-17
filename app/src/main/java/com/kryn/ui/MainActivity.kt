package com.kryn.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
        // Removes any default system window bar / decor
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KrynStorefrontApp()
        }
    }
}

data class CategoryItem(val title: String, val subtitle: String, val imageUrl: String)
data class WidgetItem(val name: String, val tag: String, val author: String, val imageUrl: String)

@Composable
fun KrynStorefrontApp() {
    val categories = listOf(
        CategoryItem("ANIME", "INK & CHERRY", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&q=80"),
        CategoryItem("MINIMAL", "SLATE & MONO", "https://images.unsplash.com/photo-1507499739999-097706ad8914?w=600&q=80"),
        CategoryItem("CYBER", "RACE TELEMETRY", "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&q=80"),
        CategoryItem("MONOLITH", "BRUTALIST ARCH", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&q=80")
    )

    val widgetShowcase = listOf(
        WidgetItem("KRYN // 01", "TELEMETRY CLOCK", "ENGINE 1.0", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
        WidgetItem("KRYN // 02", "BATTERY GAUGE", "VOLT DYNAMICS", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
        WidgetItem("KRYN // 03", "MINIMALIST MONOLITH", "PURE TYPO", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
        WidgetItem("KRYN // 04", "MEDIA DOCK", "AUDIO MATRIX", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
        WidgetItem("KRYN // 05", "HORIZON WEATHER", "RADAR HUD", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80"),
        WidgetItem("KRYN // 06", "JAPAN GP // SEKTOR", "SUZUKA TRIBUTE", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80")
    )

    Scaffold(
        containerColor = Color(0xFFF7F8FA) // Clean, modern ceramic light canvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp)
        ) {
            // 1. TOP BRAND ROW
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KRYN UI",
                            color = Color(0xFF111218),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "BY KRYN JOINT LABS",
                            color = Color(0xFFE10600), // Racing red accent
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "01",
                            color = Color(0xFF111218),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // 2. HERO SHOWCASE CARD (Light Aesthetic)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(230.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=1200&q=80",
                        contentDescription = "Hero Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Light frosted gradient fade at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xE6FFFFFF))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE10600), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DESKTOP PRECISION // ON MOBILE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "APP UNDER CONSTRUCTION",
                            color = Color(0xFF111218),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Engineered for creators, racers, and minimalists.",
                            color = Color(0xFF6B7280),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. CATEGORIES SECTION
            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPLORE BY STYLE",
                            color = Color(0xFF111218),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ALL CATEGORIES →",
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { item ->
                            CategoryCardLight(item)
                        }
                    }
                }
            }

            // 4. SIGNATURE DROPS HEADER
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIGNATURE DROPS",
                        color = Color(0xFF111218),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "v1.0 ACTIVE",
                        color = Color(0xFFE10600),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // 5. WIDGET GRID (Two columns, light cards)
            items(widgetShowcase.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { widget ->
                        Box(modifier = Modifier.weight(1f)) {
                            WidgetShowcaseCardLight(widget)
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 6. BOTTOM BRAND FOOTER
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(Color(0xFFE5E7EB))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Made by Eveleske",
                        color = Color(0xFF111218),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "From the Community to the Community",
                        color = Color(0xFF9CA3AF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCardLight(item: CategoryItem) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(18.dp))
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
                        listOf(Color(0x20000000), Color(0xCC000000))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
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
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = item.subtitle,
                    color = Color(0xFFE0E0E0),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun WidgetShowcaseCardLight(widget: WidgetItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
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
                    color = Color(0xFF111218),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = widget.name,
            color = Color(0xFF111218),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
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
                color = Color(0xFF9CA3AF),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "APPLY",
                color = Color(0xFFE10600),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
