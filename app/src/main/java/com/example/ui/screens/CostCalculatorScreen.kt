package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductionGuide
import com.example.data.repository.DeviceGuideRepository
import com.example.ui.theme.BlueprintBorder
import com.example.ui.theme.BlueprintSurface
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanDark
import com.example.ui.theme.TechEmerald

data class MedicalComparisonEntry(
    val title: String,
    val indication: String,
    val commercialBrand: String,
    val commercialPrice: Double,
    val diyPrice: Double,
    val guideId: String
)

@Composable
fun CostCalculatorScreen(
    onSelectGuide: (ProductionGuide) -> Unit,
    modifier: Modifier = Modifier
) {
    val comparisons = listOf(
        MedicalComparisonEntry(
            title = "Iontophoresis Pulse Controller",
            indication = "Palmar & Plantar Hyperhidrosis (Excessive Sweating)",
            commercialBrand = "Dermadry / RA Fischer Clinic Unit",
            commercialPrice = 499.00,
            diyPrice = 24.80,
            guideId = "preset_iontophoresis"
        ),
        MedicalComparisonEntry(
            title = "Peltier Active Palm Cooling Glove",
            indication = "Palmar Thermal Therapy & Athletic Recovery",
            commercialBrand = "Ava Core / CoreControl Vasocooling System",
            commercialPrice = 480.00,
            diyPrice = 38.20,
            guideId = "preset_cooling_glove"
        ),
        MedicalComparisonEntry(
            title = "Wearable TENS / Micro-Current Unit",
            indication = "Peripheral Neuropathy & Chronic Analgesia",
            commercialBrand = "Quell Wearable / Prescription Neuromodulator",
            commercialPrice = 249.00,
            diyPrice = 16.50,
            guideId = "preset_tens"
        ),
        MedicalComparisonEntry(
            title = "Galvanic Skin Response (GSR) Bio-Band",
            indication = "Autonomic Nervous System & Stress Biofeedback",
            commercialBrand = "Empatica Clinical Research Wearable",
            commercialPrice = 850.00,
            diyPrice = 32.00,
            guideId = "preset_iontophoresis"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Surface(
                color = BlueprintSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = TechEmerald
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COST-SLASHING MEDICAL BENCHMARKS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TechCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Commercial prescription medical hardware routinely carries 1,000% to 2,000% markups. With open engineering BOMs, patient-makers can replicate therapeutic performance safely at a fraction of the cost.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(comparisons) { entry ->
            val savings = entry.commercialPrice - entry.diyPrice
            val percentSaved = ((savings / entry.commercialPrice) * 100).toInt()

            Card(
                colors = CardDefaults.cardColors(containerColor = BlueprintSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BlueprintBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cost_card_${entry.title}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = entry.indication,
                        style = MaterialTheme.typography.bodySmall,
                        color = TechCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price Comparison Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Commercial Clinic Cost",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "$${String.format("%.0f", entry.commercialPrice)} USD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Text(
                                text = entry.commercialBrand,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "DIY Open Sourced Cost",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "$${String.format("%.2f", entry.diyPrice)} USD",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TechEmerald
                            )
                            Text(
                                text = "Savings: $${String.format("%.0f", savings)} ($percentSaved%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar showing cost reduction
                    LinearProgressIndicator(
                        progress = { (entry.diyPrice / entry.commercialPrice).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = TechEmerald,
                        trackColor = Color(0xFF334155),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val guide = when (entry.guideId) {
                                "preset_iontophoresis" -> DeviceGuideRepository.getIontophoresisDeviceGuide()
                                "preset_tens" -> DeviceGuideRepository.getTensPulseBioStimulatorGuide()
                                else -> DeviceGuideRepository.getPeltierCoolingGloveGuide()
                            }
                            onSelectGuide(guide)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("load_guide_btn_${entry.guideId}")
                    ) {
                        Text("View Production Guide & BOM", color = TechCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TechCyan
                        )
                    }
                }
            }
        }
    }
}
