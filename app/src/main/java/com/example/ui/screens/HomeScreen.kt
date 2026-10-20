package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.BuildConfig
import com.example.R
import com.example.data.model.ProductionGuide
import com.example.data.repository.DeviceGuideRepository
import com.example.network.GeminiVisionService
import com.example.ui.components.AssemblyStepCard
import com.example.ui.components.DeviceGalleryPickerDialog
import com.example.ui.components.MarkdownTableView
import com.example.ui.theme.BlueprintBorder
import com.example.ui.theme.BlueprintDark
import com.example.ui.theme.BlueprintSurface
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanDark
import com.example.ui.theme.TechEmerald
import com.example.utils.GalleryHelper
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    currentGuide: ProductionGuide?,
    onGuideGenerated: (ProductionGuide) -> Unit,
    onSaveGuide: (ProductionGuide) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // UI Scale for high visibility on any screen (defaults to 1.35f Jumbo for readability)
    var uiScale by remember { mutableFloatStateOf(1.35f) }

    fun Float.sSp(): TextUnit = (this * uiScale).sp
    fun Int.sSp(): TextUnit = (this.toFloat() * uiScale).sp

    var isAnalyzing by remember { mutableStateOf(false) }
    var statusNotice by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedPresetDrawable by remember { mutableStateOf<Int?>(null) }
    var deviceNotesInput by remember { mutableStateOf("") }
    var customApiKey by remember { mutableStateOf("") }
    var useOfflineEngineOnly by remember { mutableStateOf(true) }

    var showGalleryPickerModal by remember { mutableStateOf(false) }
    var showZoomDialog by remember { mutableStateOf(false) }
    var showEmulatorExplainer by remember { mutableStateOf(false) }

    // Step checkboxes completion state
    val stepCompletionMap = remember { mutableStateMapOf<Int, Boolean>() }
    val safetyChecklistMap = remember { mutableStateMapOf<Int, Boolean>() }

    // Direct Phone Gallery / Storage Picker launcher (Universal Android contract)
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            selectedPresetDrawable = null
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    selectedImageBitmap = BitmapFactory.decodeStream(stream)
                }
                statusNotice = "Photo loaded from gallery! Now describe your device or click Generate."
            } catch (e: Exception) {
                statusNotice = "Photo selected. Ready for analysis."
            }
        }
    }

    // Optional Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            selectedPresetDrawable = null
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    selectedImageBitmap = BitmapFactory.decodeStream(stream)
                }
                statusNotice = "Photo loaded from gallery! Ready for analysis."
            } catch (e: Exception) {
                statusNotice = "Photo selected. Ready for analysis."
            }
        }
    }

    val scrollState = rememberScrollState()

    // IN-APP CUSTOM GALLERY PICKER DIALOG FOR PRE-LOADED SCHEMATICS
    if (showGalleryPickerModal) {
        DeviceGalleryPickerDialog(
            onDismiss = { showGalleryPickerModal = false },
            onPhotoSelected = { photoItem ->
                selectedPresetDrawable = photoItem.drawableRes
                selectedImageUri = null
                selectedImageBitmap = try {
                    BitmapFactory.decodeResource(context.resources, photoItem.drawableRes)
                } catch (e: Exception) {
                    null
                }
                deviceNotesInput = photoItem.title
                val guide = photoItem.guideProvider()
                onGuideGenerated(guide)
                statusNotice = "Selected schematic: ${photoItem.title}. Guide & BOM loaded!"
                showGalleryPickerModal = false
            },
            onLaunchSystemPicker = {
                showGalleryPickerModal = false
                galleryPickerLauncher.launch("image/*")
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlueprintDark)
            .verticalScroll(scrollState)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ==========================================
        // TOP HEADER BAR & CONTROLS
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BUILD WHAT YOU SEE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sSp(),
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Wearable Electronics Production & Cost-Optimization Studio",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sSp(),
                    color = Color(0xFFCBD5E1)
                )
            }

            IconButton(
                onClick = { showEmulatorExplainer = true },
                modifier = Modifier.testTag("emulator_explainer_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = "Help and screen options",
                    tint = TechCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // ==========================================
        // DISPLAY CONTROLS & WIDESCREEN TIP
        // ==========================================
        Surface(
            color = BlueprintSurface,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TEXT SIZE:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sSp(),
                        color = TechCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = uiScale == 1.0f,
                        onClick = { uiScale = 1.0f },
                        label = { Text("Normal", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyanDark,
                            selectedLabelColor = TechCyan
                        )
                    )
                    FilterChip(
                        selected = uiScale == 1.2f,
                        onClick = { uiScale = 1.2f },
                        label = { Text("Large", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyanDark,
                            selectedLabelColor = TechCyan
                        )
                    )
                    FilterChip(
                        selected = uiScale == 1.35f,
                        onClick = { uiScale = 1.35f },
                        label = { Text("Jumbo", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechAmber,
                            selectedLabelColor = Color(0xFF0F172A)
                        )
                    )
                }
            }
        }

        // ==========================================
        // HERO WORKSTATION: UPLOAD PHOTO & DESCRIBE WHAT TO PRODUCE
        // (Directly addresses the user's primary request)
        // ==========================================
        Card(
            colors = CardDefaults.cardColors(containerColor = BlueprintSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, TechCyan),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("production_guide_builder_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Section Title Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PrecisionManufacturing,
                        contentDescription = null,
                        tint = TechAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CREATE NEW PRODUCTION GUIDE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sSp(),
                            color = TechAmber,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Upload device photo, describe your product, and generate BOM & assembly guide",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sSp(),
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ----------------------------------------------------
                // STEP 1: PLACE TO UPLOAD THE PHOTO OF WHAT TO PRODUCE
                // ----------------------------------------------------
                Text(
                    text = "📸 STEP 1: UPLOAD PHOTO OF WHAT YOU WANT TO PRODUCE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sSp(),
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upload any photo, sketch, schematic, or diagram of the device from your gallery or files:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sSp(),
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val hasImage = selectedImageUri != null || selectedPresetDrawable != null

                if (hasImage) {
                    // PREVIEW OF UPLOADED PHOTO
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color(0xFF090D16), RoundedCornerShape(10.dp))
                            .border(1.5.dp, TechCyan, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showZoomDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            selectedImageUri != null -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(selectedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Uploaded Device Photo",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            selectedPresetDrawable != null -> {
                                Image(
                                    painter = painterResource(id = selectedPresetDrawable!!),
                                    contentDescription = "Active Diagram Photo",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Badges overlay
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .background(Color(0xFF003822).copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TechEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Photo Uploaded & Ready",
                                fontSize = 11.sSp(),
                                color = TechEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Zoom badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom", tint = TechCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tap to Enlarge", fontSize = 11.sSp(), color = TechCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons when photo is loaded
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { galleryPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("change_photo_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Photo", color = TechCyan, fontSize = 13.sSp(), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showGalleryPickerModal = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("open_schematics_btn")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = TechAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schematics", color = TechAmber, fontSize = 13.sSp())
                        }

                        OutlinedButton(
                            onClick = {
                                selectedImageUri = null
                                selectedImageBitmap = null
                                selectedPresetDrawable = null
                                statusNotice = "Photo cleared. You can upload a new photo from your gallery."
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("clear_photo_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                } else {
                    // EMPTY UPLOAD DROPZONE BOX (Large, clear, and inviting)
                    Surface(
                        color = Color(0xFF090D16),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, TechCyan.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { galleryPickerLauncher.launch("image/*") }
                            .testTag("upload_dropzone")
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = TechCyan,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "UPLOAD PHOTO OF YOUR DEVICE",
                                fontSize = 16.sSp(),
                                fontWeight = FontWeight.ExtraBold,
                                color = TechCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap here to select an image from your phone gallery or files",
                                fontSize = 13.sSp(),
                                color = Color(0xFFCBD5E1)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { galleryPickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("upload_from_gallery_btn")
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Choose from Phone Gallery", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sSp())
                                }

                                OutlinedButton(
                                    onClick = { showGalleryPickerModal = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("choose_sample_schematic_btn")
                                ) {
                                    Text("Sample Schematics", color = TechAmber, fontSize = 13.sSp())
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ----------------------------------------------------
                // STEP 2: TEXT PLACE TO DESCRIBE WHAT YOU WANT TO PRODUCE
                // (Directly addresses the user's primary request)
                // ----------------------------------------------------
                Text(
                    text = "📝 STEP 2: DESCRIBE WHAT YOU WANT TO PRODUCE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sSp(),
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Specify your wearable device, intended function, target cost, or medical alternative to beat:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sSp(),
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = deviceNotesInput,
                    onValueChange = { deviceNotesInput = it },
                    label = { Text("What do you want to produce?", fontSize = 14.sSp()) },
                    placeholder = {
                        Text(
                            "e.g. A wearable iontophoresis device for hyperhidrosis using a 9V battery and conductive silicone electrodes to beat commercial $500 medical machines...",
                            fontSize = 13.sSp(),
                            color = Color(0xFF64748B)
                        )
                    },
                    minLines = 4,
                    maxLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = TechCyan.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF090D16),
                        unfocusedContainerColor = Color(0xFF090D16)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("device_description_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick prefill suggestion chips
                Text(
                    text = "Quick Suggestion Templates (Tap to fill description):",
                    fontSize = 11.sSp(),
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            deviceNotesInput = "Peltier Active Cooling Glove with Finned Aluminum Heat Sink, TEC1-12703, and APG Thermal Bus"
                            selectedPresetDrawable = R.drawable.cooling_glove_sample
                            selectedImageUri = null
                            selectedImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.cooling_glove_sample)
                        },
                        label = { Text("🧊 Cooling Glove", fontSize = 12.sSp()) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = TechCyan)
                    )
                    AssistChip(
                        onClick = {
                            deviceNotesInput = "Portable Iontophoresis Machine for Hyperhidrosis with Constant-Current Circuit and Silicone Electrodes"
                            selectedPresetDrawable = R.drawable.iontophoresis_sample
                            selectedImageUri = null
                            selectedImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.iontophoresis_sample)
                        },
                        label = { Text("⚡ Iontophoresis Device", fontSize = 12.sSp()) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = TechAmber)
                    )
                    AssistChip(
                        onClick = {
                            deviceNotesInput = "Dual-Channel TENS Bio-Stimulator with Microcurrent Pulse and Hydrogel Pads"
                            selectedPresetDrawable = R.drawable.tens_sample
                            selectedImageUri = null
                            selectedImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.tens_sample)
                        },
                        label = { Text("🩺 TENS Stimulator", fontSize = 12.sSp()) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = TechEmerald)
                    )
                    AssistChip(
                        onClick = {
                            deviceNotesInput = "Wearable Bio-Impedance Sensor Band with Skin Temperature and Sweat Sensing"
                            selectedPresetDrawable = R.drawable.bioimpedance_sample
                            selectedImageUri = null
                            selectedImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bioimpedance_sample)
                        },
                        label = { Text("📊 Wearable Sensor", fontSize = 12.sSp()) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = Color(0xFFE2E8F0))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ----------------------------------------------------
                // STEP 3: GENERATION ENGINE OPTION (FREE BUILT-IN EXPERT)
                // ----------------------------------------------------
                Surface(
                    color = Color(0xFF0F1A28),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { useOfflineEngineOnly = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = useOfflineEngineOnly,
                                onClick = { useOfflineEngineOnly = true },
                                colors = RadioButtonDefaults.colors(selectedColor = TechEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Built-in Wearable Expert (100% Free, No API Key Required)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sSp(),
                                    color = Color.White
                                )
                                Text(
                                    text = "Instant generation, offline verified formulas, beats commercial pricing",
                                    fontSize = 11.sSp(),
                                    color = TechEmerald
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { useOfflineEngineOnly = false },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !useOfflineEngineOnly,
                                onClick = { useOfflineEngineOnly = false },
                                colors = RadioButtonDefaults.colors(selectedColor = TechCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Google Gemini Multimodal AI (Optional)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sSp(),
                                    color = Color.White
                                )
                                Text(
                                    text = "Calls Gemini Vision directly if you have an API key configured",
                                    fontSize = 11.sSp(),
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        AnimatedVisibility(visible = !useOfflineEngineOnly) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                OutlinedTextField(
                                    value = customApiKey,
                                    onValueChange = { customApiKey = it },
                                    label = { Text("Gemini API Key (Optional)", fontSize = 12.sSp()) },
                                    placeholder = { Text("AIzaSy... (leave blank to use system key)") },
                                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TechCyan) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TechCyan,
                                        unfocusedBorderColor = BlueprintBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("api_key_input")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ----------------------------------------------------
                // PRIMARY GENERATE BUTTON (Massive, Bold, High-Contrast)
                // ----------------------------------------------------
                Button(
                    onClick = {
                        isAnalyzing = true
                        coroutineScope.launch {
                            val description = if (deviceNotesInput.isNotBlank()) {
                                deviceNotesInput
                            } else {
                                "Custom Reverse-Engineered Wearable Device"
                            }

                            val result = GeminiVisionService.analyzeDevice(
                                imageBitmap = selectedImageBitmap,
                                description = description,
                                customApiKey = customApiKey.ifBlank { null },
                                forceOffline = useOfflineEngineOnly
                            )

                            isAnalyzing = false
                            result.onSuccess { (guide, message) ->
                                onGuideGenerated(guide)
                                statusNotice = message
                                Toast.makeText(context, "Production Guide Generated Successfully!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechAmber,
                        disabledContainerColor = Color(0xFF4B3800)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("analyze_generate_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color(0xFF0F172A),
                            modifier = Modifier.size(26.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Analyzing Hardware & Optimizing Costs...",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sSp()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PrecisionManufacturing,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GENERATE PRODUCTION GUIDE & SOURCING LIST",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sSp()
                        )
                    }
                }

                if (statusNotice != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFF082F24),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = statusNotice ?: "",
                                fontSize = 12.sSp(),
                                color = Color(0xFFD1FAE5)
                            )
                        }
                    }
                }
            }
        }

        if (currentGuide == null) {
            Surface(
                color = Color(0xFF09111F),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("empty_guide_state_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PrecisionManufacturing,
                        contentDescription = null,
                        tint = TechAmber,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "READY TO REVERSE-ENGINEER YOUR DEVICE",
                        fontSize = 15.sSp(),
                        fontWeight = FontWeight.ExtraBold,
                        color = TechAmber,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Upload your product photo or diagram in Step 1.\n" +
                                "2. Describe what you want to produce or what commercial device to beat in Step 2.\n" +
                                "3. Tap 'GENERATE PRODUCTION GUIDE & SOURCING LIST' to see pictures of materials to buy, Amazon prices, direct buy links, and step-by-step illustrated assembly diagrams!",
                        fontSize = 12.sSp(),
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // ==========================================
        // QUICK OPTIONAL SAMPLE BLUEPRINT BUTTON
        // ==========================================
        Surface(
            color = Color(0xFF06281E),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TechEmerald.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Or Explore a Pre-loaded Blueprint?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sSp(),
                        color = Color.White
                    )
                    Text(
                        text = "Browse sample cooling glove or iontophoresis schematics",
                        fontSize = 11.sSp(),
                        color = Color(0xFFD1FAE5)
                    )
                }

                Button(
                    onClick = { showGalleryPickerModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TechEmerald),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("browse_sample_blueprints_btn")
                ) {
                    Text("Browse Blueprints", color = Color(0xFF003822), fontWeight = FontWeight.Bold, fontSize = 11.sSp())
                }
            }
        }

        // ==========================================
        // FULL-SCREEN ZOOM DIALOG FOR DIAGRAMS
        // ==========================================
        if (showZoomDialog) {
            Dialog(
                onDismissRequest = { showZoomDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    color = Color.Black,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            selectedImageUri != null -> {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Zoomed Diagram",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            selectedPresetDrawable != null -> {
                                Image(
                                    painter = painterResource(id = selectedPresetDrawable!!),
                                    contentDescription = "Zoomed Diagram",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            currentGuide?.imageDrawableRes != null -> {
                                Image(
                                    painter = painterResource(id = currentGuide.imageDrawableRes),
                                    contentDescription = "Zoomed Diagram",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        IconButton(
                            onClick = { showZoomDialog = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        // ==========================================
        // EMULATOR & SCREEN EXPLAINER DIALOG
        // ==========================================
        if (showEmulatorExplainer) {
            Dialog(onDismissRequest = { showEmulatorExplainer = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BlueprintSurface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, TechCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📱 Display & App Information",
                                fontWeight = FontWeight.Bold,
                                color = TechCyan,
                                fontSize = 18.sSp()
                            )
                            IconButton(onClick = { showEmulatorExplainer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "• Real Native Android App:\n" +
                                    "This is a complete native Android application running live on an Android emulator in the cloud.\n\n" +
                                    "• Why did it look like a phone?\n" +
                                    "Google AI Studio displays the cloud emulator in a phone container. You can rotate the emulator to landscape using the rotate button on the right toolbar for a widescreen tablet view!\n\n" +
                                    "• How to upload photos from your device:\n" +
                                    "Tap 'Choose from Gallery' in Step 1. This opens your phone's photo library or file storage immediately so you can pick any photo of what you want to produce!\n\n" +
                                    "• Text Size:\n" +
                                    "Use the 'Jumbo' text option at the top to make all text, tables, and buttons extra large and visible.",
                            fontSize = 13.sSp(),
                            color = Color(0xFFCBD5E1),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showEmulatorExplainer = false },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Close", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 14.sSp())
                        }
                    }
                }
            }
        }

        // ==========================================
        // PRODUCTION GUIDE OUTPUT SECTION
        // ==========================================
        if (currentGuide != null) {
            HorizontalDivider(color = TechCyan.copy(alpha = 0.6f), thickness = 2.dp)

            // Executive Summary & Cost Slashing Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = BlueprintSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TechEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("executive_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentGuide.deviceTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sSp(),
                                color = Color.White
                            )
                            Text(
                                text = currentGuide.deviceCategory,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 13.sSp(),
                                color = TechCyan
                            )
                        }

                        // Save Guide Action Button
                        IconButton(
                            onClick = {
                                onSaveGuide(currentGuide)
                                Toast.makeText(context, "Saved to offline guides collection!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("save_guide_icon_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdd,
                                contentDescription = "Save Guide",
                                tint = TechCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentGuide.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sSp(),
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // COST SLASHING METRICS GRID
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0A1220), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Commercial Medical Alt", fontSize = 11.sSp(), color = Color(0xFF94A3B8))
                            Text("$${String.format("%.2f", currentGuide.commercialPriceUsd)}", fontSize = 16.sSp(), fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            Text(currentGuide.commercialAlternativeName.take(22) + "...", fontSize = 10.sSp(), color = Color(0xFF64748B))
                        }

                        Column {
                            Text("Total DIY Build Cost", fontSize = 11.sSp(), color = Color(0xFF94A3B8))
                            Text("$${String.format("%.2f", currentGuide.totalBuildCostUsd)}", fontSize = 18.sSp(), fontWeight = FontWeight.ExtraBold, color = TechEmerald)
                            Text("${currentGuide.components.size} Verified BOM Parts", fontSize = 10.sSp(), color = TechEmerald)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Net Savings", fontSize = 11.sSp(), color = Color(0xFF94A3B8))
                            Text("$${String.format("%.2f", currentGuide.savingsUsd)}", fontSize = 16.sSp(), fontWeight = FontWeight.Bold, color = TechAmber)
                            Text("${currentGuide.savingsPercentage}% SAVED", fontSize = 11.sSp(), fontWeight = FontWeight.ExtraBold, color = TechAmber)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons (Copy Markdown & Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Wearable Build Guide", currentGuide.toFullMarkdown()))
                                Toast.makeText(context, "Full Markdown guide copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("copy_full_markdown_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Markdown", fontSize = 12.sSp(), color = TechCyan)
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, currentGuide.toFullMarkdown())
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Production Guide"))
                            },
                            modifier = Modifier
                                .weight(0.8f)
                                .height(46.dp)
                                .testTag("share_guide_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sSp(), color = TechCyan)
                        }
                    }
                }
            }

            // ==========================================
            // STEP 1: MATERIAL & SOURCING LIST
            // ==========================================
            MarkdownTableView(
                components = currentGuide.components,
                totalBuildCost = currentGuide.totalBuildCostUsd,
                markdownString = currentGuide.toMarkdownTable()
            )

            // ==========================================
            // STEP 2: ILLUSTRATED PRODUCTION MANUAL
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STEP 2: ILLUSTRATED PRODUCTION MANUAL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sSp(),
                            color = TechCyan
                        )
                        Text(
                            text = "${currentGuide.assemblySteps.size} Sequential Assembly Steps with 2D CAD Blueprints",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sSp(),
                            color = Color(0xFF94A3B8)
                        )
                    }

                    val completedCount = currentGuide.assemblySteps.count { stepCompletionMap[it.stepNumber] == true }
                    Text(
                        text = "$completedCount / ${currentGuide.assemblySteps.size} Built",
                        fontSize = 14.sSp(),
                        fontWeight = FontWeight.Bold,
                        color = if (completedCount == currentGuide.assemblySteps.size) TechEmerald else TechAmber,
                        fontFamily = FontFamily.Monospace
                    )
                }

                currentGuide.assemblySteps.forEach { step ->
                    val isStepCompleted = stepCompletionMap[step.stepNumber] == true
                    AssemblyStepCard(
                        step = step,
                        isCompleted = isStepCompleted,
                        onToggleCompleted = { completed ->
                            stepCompletionMap[step.stepNumber] = completed
                        }
                    )
                }
            }

            // ==========================================
            // COMMISSIONING & ELECTRICAL SAFETY CHECKLIST
            // ==========================================
            Card(
                colors = CardDefaults.cardColors(containerColor = BlueprintSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = TechEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMMISSIONING & ELECTRICAL SAFETY CHECKLIST",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sSp(),
                            color = TechEmerald,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete every verification test before applying the wearable device to patient or user skin.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sSp(),
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    currentGuide.testingChecklist.forEachIndexed { index, test ->
                        val isChecked = safetyChecklistMap[index] == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { safetyChecklistMap[index] = !isChecked }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { safetyChecklistMap[index] = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = TechEmerald,
                                    uncheckedColor = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = test,
                                fontSize = 13.sSp(),
                                color = if (isChecked) TechEmerald else Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
