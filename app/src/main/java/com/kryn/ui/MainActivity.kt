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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        containerColor = Color(0xFF090A0F)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // 1. TOP NAV BAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KRYN UI",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "BY KRYN JOINT LABS",
                            color = Color(0xFFE10600),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF161820))
                            .border(1.dp, Color(0x33FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "01",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // 2. HERO SHOWCASE CARD (Landscape Desk-Mat Style)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=1200&q=80",
                        contentDescription = "Hero Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark Vignette Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xDD090A0F))
                                )
                            )
                    )

                    // Content overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0x33E10600), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "DESKTOP PRECISION // ON MOBILE",
                                color = Color(0xFFFF4D4D),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "복잡한 화면 속,\n공간은 더 날카로워야 하니까.",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Engineered for creators, racers and minimalists.",
                            color = Color(0xFFAAAAAA),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 3. CATEGORIES SECTION (Horizontal Scroll)
            item {
                Column(modifier = Modifier.padding(top = 28.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPLORE BY STYLE",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ALL CATEGORIES →",
                            color = Color(0xFF8E8E93),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { item ->
                            CategoryCard(item)
                        }
                    }
                }
            }

            // 4. SIGNATURE WIDGET DROPS
            item {
                Column(modifier = Modifier.padding(top = 28.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SIGNATURE DROPS",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "v1.0 ACTIVE",
                            color = Color(0xFFE10600),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // 5. WIDGET GRID / CARDS
            items(widgetShowcase.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { widget ->
                        Box(modifier = Modifier.weight(1f)) {
                            WidgetShowcaseCard(widget)
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
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
                .background(Color(0x99090A0F))
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
                    color = Color(0xFFAAAAAA),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun WidgetShowcaseCard(widget: WidgetItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF10121A))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = widget.imageUrl,
                contentDescription = widget.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x40000000))
            )
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .background(Color(0xCC090A0F), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = widget.tag,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = widget.name,
            color = Color.White,
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
                color = Color(0xFF777777),
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
