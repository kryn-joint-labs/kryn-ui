package com.kryn.ui

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
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

// Typography Tokens
val CondensedDisplayFont = FontFamily(Typeface.create("sans-serif-condensed", Typeface.BOLD))
val MonoBrandFont = FontFamily.Monospace
val CleanSansFont = FontFamily(Typeface.create("sans-serif-medium", Typeface.NORMAL))

data class CategoryItem(val id: String, val title: String, val subtitle: String, val imageUrl: String)
data class WidgetItem(val name: String, val tag: String, val format: String, val category: String, val imageUrl: String)

// Navigation state destinations
sealed class Screen {
    object Home : Screen()
    object AllCategories : Screen()
    data class CategoryDetail(val category: CategoryItem) : Screen()
}

@Composable
fun KrynStorefrontApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val categories = remember {
        listOf(
            CategoryItem("minimal", "MINIMAL", "SLATE & MONO", "https://images.unsplash.com/photo-1507499739999-097706ad8914?w=600&q=80"),
            CategoryItem("anime", "ANIME", "INK & CHERRY", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&q=80"),
            CategoryItem("cyber", "CYBER", "RACE TELEMETRY", "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&q=80"),
            CategoryItem("monolith", "MONOLITH", "BRUTALIST ARCH", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&q=80")
        )
    }

    // Single source of verified widgets: Mond tribute is KRYN // LUNAR
    val signatureWidgets = remember {
        listOf(
            WidgetItem(
                name = "KRYN // LUNAR",
                tag = "STENCIL CHRONO",
                format = "4x2 // REVISION 1.0",
                category = "minimal",
                imageUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&q=85"
            )
        )
    }

    // Android hardware back-button handling
    BackHandler(enabled = currentScreen !is Screen.Home) {
        currentScreen = when (currentScreen) {
            is Screen.CategoryDetail -> Screen.AllCategories
            is Screen.AllCategories -> Screen.Home
            else -> Screen.Home
        }
    }

    Scaffold(containerColor = Color(0xFFF6F7FA)) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        categories = categories,
                        signatureWidgets = signatureWidgets,
                        onViewAllCategories = { currentScreen = Screen.AllCategories },
                        onSelectCategory = { category -> currentScreen = Screen.CategoryDetail(category) }
                    )
                }
                is Screen.AllCategories -> {
                    AllCategoriesScreen(
                        categories = categories,
                        onBack = { currentScreen = Screen.Home },
                        onSelectCategory = { category -> currentScreen = Screen.CategoryDetail(category) }
                    )
                }
                is Screen.CategoryDetail -> {
                    CategoryDetailScreen(
                        category = screen.category,
                        widgets = signatureWidgets.filter { it.category.equals(screen.category.id, ignoreCase = true) },
                        onBack = { currentScreen = Screen.AllCategories }
                    )
                }
            }
        }
    }
}

// ==========================================
// 1. HOME SCREEN
// ==========================================
@Composable
fun HomeScreen(
    categories: List<CategoryItem>,
    signatureWidgets: List<WidgetItem>,
    onViewAllCategories: () -> Unit,
    onSelectCategory: (CategoryItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 44.dp, bottom = 48.dp)
    ) {
        // TOP BRAND HEADER
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
                        Text(
                            text = "KRYN UI",
                            color = Color(0xFF0F1117),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.5.sp,
                            fontFamily = CondensedDisplayFont
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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

        // HERO CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(260.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(26.dp))
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=1200&q=85",
                    contentDescription = "Torii Scenery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x20000000), Color(0x990A0B10), Color(0xF2090A0F))
                            )
                        )
                )

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
                        text = "Directly replicate PC widgets onto your phone.",
                        color = Color(0xFFD1D5DB),
                        fontSize = 12.sp,
                        fontFamily = CleanSansFont
                    )
                }
            }
        }

        // EXPLORE BY STYLE ROW
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
                        color = Color(0xFF0F1117),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = CondensedDisplayFont
                    )
                    Text(
                        text = "ALL CATEGORIES →",
                        color = Color(0xFFE10600),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonoBrandFont,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onViewAllCategories() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(categories) { _, item ->
                        CategoryCard(item = item, onClick = { onSelectCategory(item) })
                    }
                }
            }
        }

        // SIGNATURE DROPS HEADER
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

        // REAL WIDGET DROPS
        itemsIndexed(signatureWidgets) { _, widget ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                RealWidgetCard(widget = widget)
            }
        }

        // TEASER NEXT DROP CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEAECEF))
                    .border(1.dp, Color(0xFFD8DCE3), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DROP // 02 IN PRODUCTION",
                        color = Color(0xFF0F1117),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CondensedDisplayFont,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Next release drop syncing soon from Kryn Joint Labs.",
                        color = Color(0xFF6B7280),
                        fontSize = 11.sp,
                        fontFamily = CleanSansFont
                    )
                }
            }
        }

        // FOOTER
        item {
            AppFooter()
        }
    }
}

// ==========================================
// 2. ALL CATEGORIES DIRECTORY SCREEN
// ==========================================
@Composable
fun AllCategoriesScreen(
    categories: List<CategoryItem>,
    onBack: () -> Unit,
    onSelectCategory: (CategoryItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 44.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← BACK TO LAB",
                color = Color(0xFFE10600),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MonoBrandFont,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "STYLE DIRECTORY",
                color = Color(0xFF0F1117),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                fontFamily = CondensedDisplayFont
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            items(categories) { item ->
                CategoryCard(item = item, onClick = { onSelectCategory(item) }, modifier = Modifier.fillMaxWidth().height(150.dp))
            }
        }
    }
}

// ==========================================
// 3. CATEGORY DETAIL SCREEN (With Empty State)
// ==========================================
@Composable
fun CategoryDetailScreen(
    category: CategoryItem,
    widgets: List<WidgetItem>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 44.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← DIRECTORY",
                color = Color(0xFFE10600),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MonoBrandFont,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = category.title,
                color = Color(0xFF0F1117),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = CondensedDisplayFont
            )
        }

        if (widgets.isEmpty()) {
            // FUNNY EMPTY STATE NOTICE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAECEF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "☕", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "NOTHING COOKING HERE YET",
                        color = Color(0xFF0F1117),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CondensedDisplayFont,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Engineers are aggressively typing and refueling on caffeine. Check back on the next release drop!",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp,
                        fontFamily = CleanSansFont,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(widgets) { widget ->
                    RealWidgetCard(widget = widget)
                }
            }
        }
    }
}

// ==========================================
// REUSABLE COMPONENTS
// ==========================================
@Composable
fun CategoryCard(
    item: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.width(170.dp).height(115.dp)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(20.dp))
            .clickable { onClick() }
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

@Composable
fun RealWidgetCard(widget: WidgetItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E4E9), RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = widget.imageUrl,
                contentDescription = widget.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x55000000))
            )

            // Mond preview reproduction
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "F R I D A Y",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    fontFamily = CondensedDisplayFont
                )
                Text(
                    text = "17 OCTOBER, 2026",
                    color = Color(0xEEFFFFFF),
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp,
                    fontFamily = MonoBrandFont
                )
                Text(
                    text = "- 02:40 PM -",
                    color = Color(0xAAFFFFFF),
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    fontFamily = MonoBrandFont
                )
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xE60F1117), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = widget.tag,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontFamily = MonoBrandFont,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = widget.name,
                    color = Color(0xFF0F1117),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CondensedDisplayFont
                )
                Text(
                    text = widget.format,
                    color = Color(0xFF6B7280),
                    fontSize = 9.sp,
                    fontFamily = MonoBrandFont
                )
            }

            Button(
                onClick = { /* Will trigger widget pinning */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1117)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "APPLY",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonoBrandFont
                )
            }
        }
    }
}

@Composable
fun AppFooter() {
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
