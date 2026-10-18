package com.kryn.ui

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
        requestBatteryExemptionIfNeeded(this)
        setContent {
            KrynStorefrontApp()
        }
    }
}

@SuppressLint("BatteryLife")
fun requestBatteryExemptionIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }
}

fun pinWidgetToHomeScreen(context: Context, widgetName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val receiverClass = if (widgetName.contains("VELVET", ignoreCase = true)) {
            VelvetWidgetReceiver::class.java
        } else {
            LunarWidgetReceiver::class.java
        }
        val myProvider = ComponentName(context, receiverClass)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val pinnedWidgetCallbackIntent = Intent(context, MainActivity::class.java)
            val successCallback = PendingIntent.getActivity(
                context,
                0,
                pinnedWidgetCallbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        } else {
            Toast.makeText(context, "Launcher does not support direct pinning. Long-press home screen.", Toast.LENGTH_LONG).show()
        }
    } else {
        Toast.makeText(context, "Long-press home screen to place widgets.", Toast.LENGTH_SHORT).show()
    }
}

val CondensedDisplayFont = FontFamily(Typeface.create("sans-serif-condensed", Typeface.BOLD))
val MonoBrandFont = FontFamily.Monospace
val CleanSansFont = FontFamily(Typeface.create("sans-serif-medium", Typeface.NORMAL))

data class CategoryItem(val id: String, val title: String, val subtitle: String, val imageUrl: String)
data class WidgetItem(val name: String, val tag: String, val format: String, val category: String, val imageRes: Int)

data class ColorPreset(val name: String, val hex: Long)

sealed class Screen {
    object Home : Screen()
    object AllCategories : Screen()
    data class CategoryDetail(val category: CategoryItem) : Screen()
    data class VelvetCustomizer(val widget: WidgetItem) : Screen()
}

@Composable
fun KrynStorefrontApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val categories = remember {
        listOf(
            CategoryItem("minimal", "MINIMAL", "SLATE & MONO", "https://images.unsplash.com/photo-1507499739999-097706ad8914?w=600&q=80"),
            CategoryItem("anime", "ANIME", "INK & CHERRY", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&q=80"),
            CategoryItem("cyber", "CYBER", "RACE TELEMETRY", "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&q=80"),
            CategoryItem("monolith", "MONOLITH", "BRUTALIST ARCH", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&q=80")
        )
    }

    val signatureWidgets = remember {
        listOf(
            WidgetItem(
                name = "KRYN // LUNAR",
                tag = "STENCIL CHRONO",
                format = "4x2 // REVISION 1.2",
                category = "minimal",
                imageRes = R.drawable.preview_lunar
            ),
            WidgetItem(
                name = "KRYN // VELVET",
                tag = "SCRIPT LAVENDER",
                format = "4x4 // REVISION 1.0",
                category = "minimal",
                imageRes = R.drawable.preview_velvet
            )
        )
    }

    BackHandler(enabled = currentScreen !is Screen.Home) {
        currentScreen = when (currentScreen) {
            is Screen.VelvetCustomizer -> Screen.Home
            is Screen.CategoryDetail -> Screen.AllCategories
            is Screen.AllCategories -> Screen.Home
            else -> Screen.Home
        }
    }

    Scaffold(containerColor = Color(0xFFF6F7FA)) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (slideInHorizontally(
                        initialOffsetX = { fullWidth -> (fullWidth * 0.35f).toInt() },
                        animationSpec = tween(420, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(380))).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> (-fullWidth * 0.25f).toInt() },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(280))
                    )
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            categories = categories,
                            signatureWidgets = signatureWidgets,
                            onViewAllCategories = { currentScreen = Screen.AllCategories },
                            onSelectCategory = { category -> currentScreen = Screen.CategoryDetail(category) },
                            onWidgetApply = { widget ->
                                if (widget.name.contains("VELVET", ignoreCase = true)) {
                                    currentScreen = Screen.VelvetCustomizer(widget)
                                } else {
                                    pinWidgetToHomeScreen(context, widget.name)
                                }
                            }
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
                            category = targetScreen.category,
                            widgets = signatureWidgets.filter { it.category.equals(targetScreen.category.id, ignoreCase = true) },
                            onBack = { currentScreen = Screen.AllCategories },
                            onWidgetApply = { widget ->
                                if (widget.name.contains("VELVET", ignoreCase = true)) {
                                    currentScreen = Screen.VelvetCustomizer(widget)
                                } else {
                                    pinWidgetToHomeScreen(context, widget.name)
                                }
                            }
                        )
                    }
                    is Screen.VelvetCustomizer -> {
                        VelvetCustomizerScreen(
                            widget = targetScreen.widget,
                            onBack = { currentScreen = Screen.Home }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VelvetCustomizerScreen(
    widget: WidgetItem,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("kryn_velvet_prefs", Context.MODE_PRIVATE) }

    val dayPresets = listOf(
        ColorPreset("Lavender", 0xFFB84DFF),
        ColorPreset("Pure White", 0xFFFFFFFF),
        ColorPreset("Neon Cyan", 0xFF00E5FF),
        ColorPreset("Coral Red", 0xFFFF3B30)
    )

    val timePresets = listOf(
        ColorPreset("Ghost White", 0xFFFFFFFF),
        ColorPreset("Charcoal", 0xFF111218),
        ColorPreset("Silver", 0xFF9E9E9E),
        ColorPreset("Pure Black", 0xFF000000)
    )

    var selectedDayColor by remember {
        mutableStateOf(prefs.getLong("velvet_color_day", 0xFFB84DFF))
    }
    var selectedTimeColor by remember {
        mutableStateOf(prefs.getLong("velvet_color_time", 0xFFFFFFFF))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 44.dp, bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← CANCEL",
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
                    text = "CONFIGURE VELVET",
                    color = Color(0xFF0F1117),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = CondensedDisplayFont
                )
            }
        }

        // Live Preview Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0D0E12))
                    .border(1.dp, Color(0xFF22242B), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "4:20",
                            color = Color(selectedTimeColor).copy(alpha = 0.25f),
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = CondensedDisplayFont
                        )
                        Text(
                            text = "Sunday",
                            color = Color(selectedDayColor),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(1.5.dp)
                            .background(Color(selectedTimeColor).copy(alpha = 0.4f))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "18 OCTOBER 2026",
                        color = Color(selectedTimeColor).copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        fontFamily = MonoBrandFont
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Color 1: Day (Script)
        item {
            Text(
                text = "COLOR 1 (DAY SCRIPT)",
                color = Color(0xFF0F1117),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontFamily = MonoBrandFont
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                dayPresets.forEach { preset ->
                    val isSelected = selectedDayColor == preset.hex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(preset.hex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF0F1117) else Color(0x33000000),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedDayColor = preset.hex }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Color 2: Time & Date
        item {
            Text(
                text = "COLOR 2 (TIME & DATE)",
                color = Color(0xFF0F1117),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontFamily = MonoBrandFont
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                timePresets.forEach { preset ->
                    val isSelected = selectedTimeColor == preset.hex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(preset.hex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFFE10600) else Color(0x33000000),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTimeColor = preset.hex }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tip: Choose White for dark wallpapers, or Charcoal/Black for light wallpapers.",
                color = Color(0xFF6B7280),
                fontSize = 11.sp,
                fontFamily = CleanSansFont
            )
            Spacer(modifier = Modifier.height(30.dp))
        }

        // Confirm & Pin Button
        item {
            Button(
                onClick = {
                    prefs.edit()
                        .putLong("velvet_color_day", selectedDayColor)
                        .putLong("velvet_color_time", selectedTimeColor)
                        .apply()

                    val updateIntent = Intent(context, VelvetWidgetReceiver::class.java).apply {
                        action = VelvetWidgetReceiver.ACTION_MINUTE_TICK
                    }
                    context.sendBroadcast(updateIntent)

                    pinWidgetToHomeScreen(context, widget.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1117)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "SAVE & PIN TO HOME SCREEN",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonoBrandFont,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    categories: List<CategoryItem>,
    signatureWidgets: List<WidgetItem>,
    onViewAllCategories: () -> Unit,
    onSelectCategory: (CategoryItem) -> Unit,
    onWidgetApply: (WidgetItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 44.dp, bottom = 48.dp)
    ) {
        item {
            EntranceContainer(delay = 50) {
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
                            text = "BY KRYN JOINT LABS // ENGINE v1.2",
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
        }

        item {
            EntranceContainer(delay = 120) {
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
        }

        item {
            EntranceContainer(delay = 180) {
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
                        itemsIndexed(categories) { index, item ->
                            EntranceContainer(delay = 200 + (index * 60)) {
                                CategoryCard(item = item, onClick = { onSelectCategory(item) })
                            }
                        }
                    }
                }
            }
        }

        item {
            EntranceContainer(delay = 250) {
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
                        text = "v1.2 ACTIVE",
                        color = Color(0xFFE10600),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonoBrandFont
                    )
                }
            }
        }

        itemsIndexed(signatureWidgets) { index, widget ->
            EntranceContainer(delay = 300 + (index * 70)) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    RealWidgetCard(
                        widget = widget,
                        onApplyClick = { onWidgetApply(widget) }
                    )
                }
            }
        }

        item {
            EntranceContainer(delay = 400) {
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
                            text = "DROP // 03 IN PRODUCTION",
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
        }

        item {
            EntranceContainer(delay = 460) {
                AppFooter()
            }
        }
    }
}

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
        EntranceContainer(delay = 50) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
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
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            itemsIndexed(categories) { index, item ->
                EntranceContainer(delay = 100 + (index * 60)) {
                    CategoryCard(
                        item = item,
                        onClick = { onSelectCategory(item) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryDetailScreen(
    category: CategoryItem,
    widgets: List<WidgetItem>,
    onBack: () -> Unit,
    onWidgetApply: (WidgetItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 44.dp, start = 20.dp, end = 20.dp)
    ) {
        EntranceContainer(delay = 50) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
        }

        if (widgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EntranceContainer(delay = 120) {
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
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(widgets) { index, widget ->
                    EntranceContainer(delay = 120 + (index * 70)) {
                        RealWidgetCard(
                            widget = widget,
                            onApplyClick = { onWidgetApply(widget) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EntranceContainer(
    delay: Int,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "AlphaAnim"
    )

    val animatedTranslateY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 32f,
        animationSpec = tween(
            durationMillis = 550,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "TranslateAnim"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.97f,
        animationSpec = tween(
            durationMillis = 550,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "ScaleAnim"
    )

    Box(
        modifier = Modifier
            .offset(y = animatedTranslateY.dp)
            .scale(animatedScale)
            .alpha(animatedAlpha)
    ) {
        content()
    }
}

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
fun RealWidgetCard(
    widget: WidgetItem,
    onApplyClick: () -> Unit
) {
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
                .background(Color(0xFF090A0F))
        ) {
            Image(
                painter = painterResource(id = widget.imageRes),
                contentDescription = widget.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

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
                onClick = { onApplyClick() },
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
