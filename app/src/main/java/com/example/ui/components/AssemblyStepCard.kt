package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssemblyStep
import com.example.ui.theme.BlueprintBorder
import com.example.ui.theme.BlueprintSurface
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanDark
import com.example.ui.theme.TechEmerald

@Composable
fun AssemblyStepCard(
    step: AssemblyStep,
    isCompleted: Boolean,
    onToggleCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPromptDetails by remember { mutableStateOf(false) }

    Surface(
        color = BlueprintSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) TechEmerald.copy(alpha = 0.6f) else BlueprintBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("assembly_step_card_${step.stepNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Step Badge, Title & Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) TechEmerald else TechCyanDark),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color(0xFF003822),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "${step.stepNumber}",
                            color = TechCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STEP ${step.stepNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted) TechEmerald else TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = onToggleCompleted,
                    colors = CheckboxDefaults.colors(
                        checkedColor = TechEmerald,
                        uncheckedColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("step_checkbox_${step.stepNumber}")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Punchy Instructions List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF090E1A), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                step.punchyInstructions.forEach { instruction ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "▸ ",
                            color = TechCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2D Line-Art Technical Blueprint Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ILLUSTRATED STEP BLUEPRINT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace
                )
                OutlinedButton(
                    onClick = { showPromptDetails = !showPromptDetails },
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (showPromptDetails) "Compact View" else "Enlarge Blueprint",
                        fontSize = 10.sp,
                        color = TechCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2D Line-Art Technical Blueprint Canvas
            TechnicalDiagramCanvas(
                diagramType = step.diagramType,
                height = if (showPromptDetails) 280.dp else 170.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // AI Image Generator Prompt Label Box (MANDATORY REQUIREMENT)
            Surface(
                color = Color(0xFF10192A),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = TechCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Image Generator Prompt Label",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechCyan
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("AI Diagram Prompt", step.aiImagePromptLabel))
                                Toast.makeText(context, "AI Image prompt copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("copy_prompt_step_${step.stepNumber}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy AI Prompt",
                                tint = TechCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\"${step.aiImagePromptLabel}\"",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ELECTRICAL SAFETY PROTOCOL (MANDATORY REQUIREMENT: Emphasized insulation)
            Surface(
                color = Color(0xFF1E2816),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechEmerald.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Electrical Safety",
                        tint = TechEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "⚡ ELECTRICAL SAFETY & INSULATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechEmerald,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = step.electricalSafetyProtocol,
                            fontSize = 11.sp,
                            color = Color(0xFFD1FAE5),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // HEAT MANAGEMENT PROTOCOL (MANDATORY REQUIREMENT: Emphasized heat management)
            Surface(
                color = Color(0xFF2B1A0F),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechAmber.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Heat Management",
                        tint = TechAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🌡️ THERMAL / HEAT DISSIPATION MANAGEMENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechAmber,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = step.heatManagementProtocol,
                            fontSize = 11.sp,
                            color = Color(0xFFFEF3C7),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
