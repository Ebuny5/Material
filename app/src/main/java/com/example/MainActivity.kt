package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.model.ProductionGuide
import com.example.data.repository.DeviceGuideRepository
import com.example.ui.screens.CostCalculatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SavedGuidesScreen
import com.example.ui.theme.BlueprintDark
import com.example.ui.theme.BlueprintSurface
import com.example.ui.theme.BuildWhatYouSeeTheme
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechEmerald
import com.example.utils.GalleryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Seed virtual emulator gallery with blueprints in background
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                GalleryHelper.populateEmulatorGallery(this@MainActivity)
            } catch (e: Exception) {
                // Ignore if permission or storage error
            }
        }

        setContent {
            BuildWhatYouSeeTheme(darkTheme = true) {
                BuildWhatYouSeeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildWhatYouSeeApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Start empty so the user uploads and describes their own product without a guessed product pre-loaded
    var currentGuide by remember {
        mutableStateOf<ProductionGuide?>(null)
    }

    // Saved guides list in memory initialized with the two core presets
    val savedGuides = remember {
        mutableStateListOf<ProductionGuide>().apply {
            add(DeviceGuideRepository.getPeltierCoolingGloveGuide())
            add(DeviceGuideRepository.getIontophoresisDeviceGuide())
        }
    }

    // BackHandler to return to home tab
    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth > 650.dp

        if (isWide) {
            // ==========================================
            // WIDE TABLET / DESKTOP / LANDSCAPE LAYOUT
            // Replaces phone layout with full-page workstation view
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueprintDark)
            ) {
                NavigationRail(
                    containerColor = BlueprintSurface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .testTag("main_navigation_rail")
                ) {
                    Text(
                        text = "BUILD",
                        color = TechCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationRailItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PrecisionManufacturing,
                                contentDescription = "Build Guide"
                            )
                        },
                        label = { Text("Build", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = TechCyan,
                            selectedTextColor = TechCyan,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFF0D2535)
                        )
                    )

                    NavigationRailItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = "Cost Benchmarks"
                            )
                        },
                        label = { Text("Costs", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = TechEmerald,
                            selectedTextColor = TechEmerald,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFF0A2B1D)
                        )
                    )

                    NavigationRailItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Bookmarks,
                                contentDescription = "Saved Guides"
                            )
                        },
                        label = { Text("Saved", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = TechAmber,
                            selectedTextColor = TechAmber,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFF322300)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(BlueprintDark)
                ) {
                    when (selectedTab) {
                        0 -> {
                            HomeScreen(
                                currentGuide = currentGuide,
                                onGuideGenerated = { guide -> currentGuide = guide },
                                onSaveGuide = { guide ->
                                    if (savedGuides.none { it.id == guide.id }) {
                                        savedGuides.add(guide)
                                    }
                                }
                            )
                        }
                        1 -> {
                            CostCalculatorScreen(
                                onSelectGuide = { guide ->
                                    currentGuide = guide
                                    selectedTab = 0
                                }
                            )
                        }
                        2 -> {
                            SavedGuidesScreen(
                                savedGuides = savedGuides,
                                onSelectGuide = { guide ->
                                    currentGuide = guide
                                    selectedTab = 0
                                },
                                onDeleteGuide = { guide ->
                                    savedGuides.removeAll { it.id == guide.id }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // COMPACT PORTRAIT LAYOUT
            // ==========================================
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "BUILD WHAT YOU SEE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = TechCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        actions = {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF0F2722), androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "FREE MODE",
                                    color = TechEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = BlueprintSurface,
                            titleContentColor = TechCyan
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = BlueprintSurface,
                        modifier = Modifier.testTag("main_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PrecisionManufacturing,
                                    contentDescription = "Build Guide"
                                )
                            },
                            label = { Text("Build Guide", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechCyan,
                                selectedTextColor = TechCyan,
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B),
                                indicatorColor = Color(0xFF0D2535)
                            ),
                            modifier = Modifier.testTag("nav_tab_build_guide")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = "Cost Benchmarks"
                                )
                            },
                            label = { Text("Cost Slashing", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechEmerald,
                                selectedTextColor = TechEmerald,
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B),
                                indicatorColor = Color(0xFF0A2B1D)
                            ),
                            modifier = Modifier.testTag("nav_tab_cost_benchmarks")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Bookmarks,
                                    contentDescription = "Saved Guides"
                                )
                            },
                            label = { Text("Saved (${savedGuides.size})", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechAmber,
                                selectedTextColor = TechAmber,
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B),
                                indicatorColor = Color(0xFF322300)
                            ),
                            modifier = Modifier.testTag("nav_tab_saved_guides")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(BlueprintDark)
                ) {
                    when (selectedTab) {
                        0 -> {
                            HomeScreen(
                                currentGuide = currentGuide,
                                onGuideGenerated = { guide -> currentGuide = guide },
                                onSaveGuide = { guide ->
                                    if (savedGuides.none { it.id == guide.id }) {
                                        savedGuides.add(guide)
                                    }
                                }
                            )
                        }
                        1 -> {
                            CostCalculatorScreen(
                                onSelectGuide = { guide ->
                                    currentGuide = guide
                                    selectedTab = 0
                                }
                            )
                        }
                        2 -> {
                            SavedGuidesScreen(
                                savedGuides = savedGuides,
                                onSelectGuide = { guide ->
                                    currentGuide = guide
                                    selectedTab = 0
                                },
                                onDeleteGuide = { guide ->
                                    savedGuides.removeAll { it.id == guide.id }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
