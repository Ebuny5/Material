package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.ProductionGuide
import com.example.data.repository.DeviceGuideRepository
import com.example.ui.theme.BlueprintBorder
import com.example.ui.theme.BlueprintDark
import com.example.ui.theme.BlueprintSurface
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanDark
import com.example.ui.theme.TechEmerald
import com.example.utils.GalleryHelper

data class GalleryPhotoItem(
    val id: String,
    val title: String,
    val category: String,
    val drawableRes: Int,
    val guideProvider: () -> ProductionGuide
)

@Composable
fun DeviceGalleryPickerDialog(
    onDismiss: () -> Unit,
    onPhotoSelected: (GalleryPhotoItem) -> Unit,
    onLaunchSystemPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val photoItems = remember {
        listOf(
            GalleryPhotoItem(
                id = "peltier_glove",
                title = "Peltier Cooling Glove",
                category = "Active Palm Thermal Regulator",
                drawableRes = R.drawable.cooling_glove_sample,
                guideProvider = { DeviceGuideRepository.getPeltierCoolingGloveGuide() }
            ),
            GalleryPhotoItem(
                id = "iontophoresis",
                title = "Iontophoresis Medical Controller",
                category = "Hyperhidrosis Constant-Current",
                drawableRes = R.drawable.iontophoresis_sample,
                guideProvider = { DeviceGuideRepository.getIontophoresisDeviceGuide() }
            ),
            GalleryPhotoItem(
                id = "tens_stimulator",
                title = "Wearable TENS Stimulator",
                category = "Neuromodulation Analgesia",
                drawableRes = R.drawable.tens_sample,
                guideProvider = { DeviceGuideRepository.getTensPulseBioStimulatorGuide() }
            ),
            GalleryPhotoItem(
                id = "bioimpedance",
                title = "Bio-Impedance Sensor Band",
                category = "GSR Tissue Impedance",
                drawableRes = R.drawable.bioimpedance_sample,
                guideProvider = { DeviceGuideRepository.getBioImpedanceSensorBandGuide() }
            ),
            GalleryPhotoItem(
                id = "neck_cooler",
                title = "Thermoelectric Neck Cooler",
                category = "Carotid Dual-Peltier Collar",
                drawableRes = R.drawable.neck_cooler_sample,
                guideProvider = {
                    DeviceGuideRepository.analyzeCustomDeviceOffline(
                        "Wearable Thermoelectric Neck Cooler Collar with Dual Peltier Plates"
                    )
                }
            ),
            GalleryPhotoItem(
                id = "sweat_patch",
                title = "Microfluidic Sweat Biosensor",
                category = "Electrochemical Diagnostic Skin Patch",
                drawableRes = R.drawable.sweat_patch_sample,
                guideProvider = {
                    DeviceGuideRepository.analyzeCustomDeviceOffline(
                        "Wearable Microfluidic Biosensing Patch with Colorimetric & Electrochemical Wells"
                    )
                }
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = BlueprintDark,
            modifier = modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, TechCyan, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PHOTO GALLERY & SCHEMATICS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TechCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Select any device diagram to load & generate guide",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_gallery_picker_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val count = GalleryHelper.populateEmulatorGallery(context)
                            Toast.makeText(
                                context,
                                "Successfully saved $count blueprints to your virtual phone's camera roll!",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_all_to_system_gallery_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF003822))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save All to Phone Gallery",
                            color = Color(0xFF003822),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onLaunchSystemPicker,
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("open_system_picker_btn")
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = TechCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Browse Device Files",
                            color = TechCyan,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BlueprintBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "TAP ANY DIAGRAM BELOW TO SELECT:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechAmber,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Photo Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(photoItems) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BlueprintSurface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BlueprintBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPhotoSelected(item)
                                    onDismiss()
                                }
                                .testTag("gallery_item_${item.id}")
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black)
                                ) {
                                    Image(
                                        painter = painterResource(id = item.drawableRes),
                                        contentDescription = item.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = item.category,
                                    fontSize = 11.sp,
                                    color = TechCyan,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = TechCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
