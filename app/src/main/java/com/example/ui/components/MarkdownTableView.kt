package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComponentCategory
import com.example.data.model.ComponentItem
import com.example.ui.theme.BlueprintBorder
import com.example.ui.theme.BlueprintSurface
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanDark
import com.example.ui.theme.TechEmerald

@Composable
fun MarkdownTableView(
    components: List<ComponentItem>,
    totalBuildCost: Double,
    markdownString: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf("cards") } // "cards", "table", "raw_markdown"
    var selectedCategory by remember { mutableStateOf<ComponentCategory?>(null) }

    val filteredList = remember(components, selectedCategory) {
        if (selectedCategory == null) components else components.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BlueprintSurface, RoundedCornerShape(12.dp))
            .border(1.dp, BlueprintBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Header & View Mode Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STEP 1: MATERIAL & SOURCING LIST",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechCyan
                )
                Text(
                    text = "${components.size} Materials to Buy // Prices on Amazon & Retailers",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Markdown BOM Table", markdownString))
                    Toast.makeText(context, "Markdown Table copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("copy_markdown_table_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Markdown Table",
                    tint = TechCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle buttons (Cards with Material Photos vs Data Table vs Raw Markdown)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewMode = "cards" },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (viewMode == "cards") TechCyan.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (viewMode == "cards") TechCyan else BlueprintBorder),
                modifier = Modifier.weight(1f).testTag("view_mode_cards_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ViewList, contentDescription = null, modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Photos & Amazon", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { viewMode = "table" },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (viewMode == "table") TechCyan.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (viewMode == "table") TechCyan else BlueprintBorder),
                modifier = Modifier.weight(1f).testTag("view_mode_table_btn")
            ) {
                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Table", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { viewMode = "raw_markdown" },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (viewMode == "raw_markdown") TechCyan.copy(alpha = 0.15f) else Color.Transparent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (viewMode == "raw_markdown") TechCyan else BlueprintBorder),
                modifier = Modifier.weight(1f).testTag("view_mode_markdown_btn")
            ) {
                Text("Markdown", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All (${components.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TechCyanDark,
                    selectedLabelColor = TechCyan
                )
            )
            ComponentCategory.entries.forEach { category ->
                val count = components.count { it.category == category }
                if (count > 0) {
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category },
                        label = { Text("${category.label} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyanDark,
                            selectedLabelColor = TechCyan
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (viewMode) {
            "cards" -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredList.forEach { item ->
                        ComponentCardRow(item = item, context = context)
                    }
                }
            }
            "table" -> {
                ComponentGridTable(items = filteredList, totalCost = totalBuildCost)
            }
            "raw_markdown" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF090D16), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = markdownString,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TechEmerald,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = BlueprintBorder)
        Spacer(modifier = Modifier.height(10.dp))

        // Total Cost Sourcing Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F2236), RoundedCornerShape(8.dp))
                .border(1.dp, TechCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOTAL ESTIMATED BUILD COST",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechCyan
                )
                Text(
                    text = "Includes all verified materials & Amazon-sourced parts",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Text(
                text = "$${String.format("%.2f", totalBuildCost)} USD",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TechEmerald
            )
        }
    }
}

@Composable
private fun ComponentCardRow(
    item: ComponentItem,
    context: Context
) {
    Surface(
        color = Color(0xFF0D1526),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth().testTag("component_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Material Thumbnail Picture (Shows real picture of material to buy)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF070B14))
                    .border(1.dp, TechCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageDrawableRes != null) {
                    Image(
                        painter = painterResource(id = item.imageDrawableRes),
                        contentDescription = "Material photo: ${item.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Category icon fallback
                    Icon(
                        imageVector = when (item.category) {
                            ComponentCategory.THERMAL -> Icons.Default.AcUnit
                            ComponentCategory.ELECTRONICS -> Icons.Default.Memory
                            ComponentCategory.ELECTRODES -> Icons.Default.Bolt
                            ComponentCategory.CHASSIS -> Icons.Default.Layers
                            ComponentCategory.FASTENERS -> Icons.Default.Hardware
                        },
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name & Specification
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = item.specificMaterialOrModel,
                    style = MaterialTheme.typography.bodySmall,
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price on Amazon Pill & Sourcing Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xFF2E1C03),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechAmber)
                    ) {
                        Text(
                            text = "Amazon: $${String.format("%.2f", item.amazonPriceUsd)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TechAmber,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "• ${item.whereToBuy}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                if (item.notes.isNotBlank()) {
                    Text(
                        text = item.notes,
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons: Buy on Amazon & Web Search
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.amazonUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechAmber),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp).testTag("buy_amazon_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Buy on Amazon",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val query = Uri.encode("${item.name} ${item.specificMaterialOrModel}")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Find Retailers",
                            fontSize = 11.sp,
                            color = TechCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentGridTable(
    items: List<ComponentItem>,
    totalCost: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BlueprintBorder, RoundedCornerShape(8.dp))
            .horizontalScroll(rememberScrollState())
    ) {
        Column {
            // Table Header Row
            Row(
                modifier = Modifier
                    .background(Color(0xFF09111F))
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = "COMPONENT NAME",
                    modifier = Modifier.width(180.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "SPECIFIC MATERIAL / MODEL",
                    modifier = Modifier.width(220.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "AMAZON PRICE (USD)",
                    modifier = Modifier.width(130.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "RETAILERS / SOURCING",
                    modifier = Modifier.width(160.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(color = BlueprintBorder)

            // Table Data Rows
            items.forEachIndexed { index, item ->
                val bg = if (index % 2 == 0) Color(0xFF0D1526) else Color(0xFF09101E)
                Row(
                    modifier = Modifier
                        .background(bg)
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.width(180.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = item.specificMaterialOrModel,
                        modifier = Modifier.width(220.dp),
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "$${String.format("%.2f", item.amazonPriceUsd)}",
                        modifier = Modifier.width(130.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechEmerald
                    )
                    Text(
                        text = item.whereToBuy,
                        modifier = Modifier.width(160.dp),
                        fontSize = 11.sp,
                        color = TechAmber
                    )
                }
                HorizontalDivider(color = Color(0xFF1E293B).copy(alpha = 0.5f))
            }
        }
    }
}
