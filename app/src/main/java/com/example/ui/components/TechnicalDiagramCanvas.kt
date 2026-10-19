package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiagramType
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyan

@Composable
fun TechnicalDiagramCanvas(
    diagramType: DiagramType,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 180.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Color(0xFF090D16), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawBlueprintGrid()
            when (diagramType) {
                DiagramType.EXPLODED_VIEW -> drawExplodedViewDiagram()
                DiagramType.WIRING_SCHEMATIC -> drawWiringSchematicDiagram()
                DiagramType.THERMAL_BUS_INTERFACE -> drawThermalBusDiagram()
                DiagramType.INSULATION_AND_ENCLOSURE -> drawEnclosureDiagram()
                DiagramType.TESTING_AND_VERIFICATION -> drawTestingVerificationDiagram()
            }
        }

        Text(
            text = "2D TECHNICAL BLUEPRINT // ${diagramType.name.replace("_", " ")}",
            color = TechCyan.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        )

        Text(
            text = "SCALE 1:1 [CAD LINE-ART]",
            color = Color(0xFF64748B),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
        )
    }
}

private fun DrawScope.drawBlueprintGrid() {
    val step = 20.dp.toPx()
    val gridColor = Color(0xFF131E33)
    var x = 0f
    while (x < size.width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
        x += step
    }
    var y = 0f
    while (y < size.height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += step
    }
}

private fun DrawScope.drawExplodedViewDiagram() {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val lineColor = Color(0xFFE2E8F0)
    val accentColor = TechCyan
    val dashed = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)

    // Center vertical alignment axis
    drawLine(
        color = Color(0xFF475569),
        start = Offset(cx, 20f),
        end = Offset(cx, size.height - 20f),
        strokeWidth = 1.5f,
        pathEffect = dashed
    )

    // Top: Finned Heat Sink
    val hsWidth = 90f
    val hsHeight = 28f
    val hsY = cy - 50f
    // Base plate
    drawRect(
        color = lineColor,
        topLeft = Offset(cx - hsWidth / 2, hsY),
        size = Size(hsWidth, 8f),
        style = Stroke(2f)
    )
    // Vertical fins
    for (i in 0..5) {
        val finX = cx - hsWidth / 2 + 10f + i * 14f
        drawLine(
            color = lineColor,
            start = Offset(finX, hsY),
            end = Offset(finX, hsY - hsHeight),
            strokeWidth = 2f
        )
    }

    // Thermal Paste layer indicator
    drawRoundRect(
        color = TechAmber,
        topLeft = Offset(cx - 30f, cy - 20f),
        size = Size(60f, 4f),
        cornerRadius = CornerRadius(2f, 2f),
        style = Stroke(1.5f)
    )

    // Middle: Peltier TEC module
    drawRoundRect(
        color = accentColor,
        topLeft = Offset(cx - 35f, cy - 10f),
        size = Size(70f, 16f),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(2f)
    )
    // Internal thermoelectric couples line
    drawLine(
        color = accentColor.copy(alpha = 0.5f),
        start = Offset(cx - 30f, cy - 2f),
        end = Offset(cx + 30f, cy - 2f),
        strokeWidth = 1f,
        pathEffect = dashed
    )

    // Bottom: Copper Heat Bus / Mounting Flange
    drawRect(
        color = lineColor,
        topLeft = Offset(cx - 75f, cy + 22f),
        size = Size(150f, 12f),
        style = Stroke(2f)
    )

    // Dimension callout lines
    drawCalloutLeader(
        start = Offset(cx + 45f, hsY - 14f),
        bend = Offset(cx + 80f, hsY - 14f),
        end = Offset(cx + 120f, hsY - 14f),
        color = Color(0xFF94A3B8)
    )
}

private fun DrawScope.drawWiringSchematicDiagram() {
    val cy = size.height / 2f
    val startX = size.width * 0.15f
    val endX = size.width * 0.85f
    val wireColor = Color(0xFFE2E8F0)
    val accentColor = TechCyan
    val groundColor = Color(0xFF64748B)

    // Battery Symbol (Left)
    val battX = startX
    drawLine(wireColor, Offset(battX, cy - 20f), Offset(battX, cy + 20f), 2f)
    drawLine(wireColor, Offset(battX + 8f, cy - 12f), Offset(battX + 8f, cy + 12f), 3.5f)
    drawLine(wireColor, Offset(battX + 16f, cy - 20f), Offset(battX + 16f, cy + 20f), 2f)
    drawLine(wireColor, Offset(battX + 24f, cy - 12f), Offset(battX + 24f, cy + 12f), 3.5f)

    // Top Power Rail (+3.7V)
    drawLine(wireColor, Offset(battX, cy - 20f), Offset(battX, cy - 40f), 2f)
    drawLine(wireColor, Offset(battX, cy - 40f), Offset(endX, cy - 40f), 2f)

    // Switch Symbol on Top Rail
    val swX = size.width * 0.42f
    drawCircle(Color(0xFF090D16), 4f, Offset(swX, cy - 40f))
    drawCircle(wireColor, 4f, Offset(swX, cy - 40f), style = Stroke(1.5f))
    drawLine(TechAmber, Offset(swX + 2f, cy - 40f), Offset(swX + 24f, cy - 52f), 2.5f, cap = StrokeCap.Round)
    drawCircle(wireColor, 4f, Offset(swX + 28f, cy - 40f), style = Stroke(1.5f))

    // Middle Load Box (TEC / Driver Block)
    val loadX = size.width * 0.65f
    drawRect(
        color = accentColor,
        topLeft = Offset(loadX - 25f, cy - 18f),
        size = Size(50f, 36f),
        style = Stroke(2f)
    )
    // Symbol inside load
    drawLine(accentColor, Offset(loadX - 15f, cy), Offset(loadX + 15f, cy), 1.5f)

    // Connections to Load
    drawLine(wireColor, Offset(loadX, cy - 40f), Offset(loadX, cy - 18f), 2f)
    drawLine(wireColor, Offset(loadX, cy + 18f), Offset(loadX, cy + 40f), 2f)

    // Bottom Return Rail (GND)
    drawLine(wireColor, Offset(battX, cy + 20f), Offset(battX, cy + 40f), 2f)
    drawLine(wireColor, Offset(battX, cy + 40f), Offset(endX, cy + 40f), 2f)

    // Earth GND symbol
    val gndX = size.width * 0.5f
    val gndY = cy + 40f
    drawLine(groundColor, Offset(gndX, gndY), Offset(gndX, gndY + 12f), 2f)
    drawLine(groundColor, Offset(gndX - 14f, gndY + 12f), Offset(gndX + 14f, gndY + 12f), 2f)
    drawLine(groundColor, Offset(gndX - 9f, gndY + 16f), Offset(gndX + 9f, gndY + 16f), 2f)
    drawLine(groundColor, Offset(gndX - 4f, gndY + 20f), Offset(gndX + 4f, gndY + 20f), 2f)
}

private fun DrawScope.drawThermalBusDiagram() {
    val cy = size.height / 2f
    val lineColor = Color(0xFFE2E8F0)
    val thermalColor = TechAmber
    val coolColor = TechCyan

    // Flexible curved bus ribbon
    val path = Path().apply {
        moveTo(40f, cy + 30f)
        cubicTo(
            size.width * 0.3f, cy + 35f,
            size.width * 0.45f, cy - 25f,
            size.width * 0.75f, cy - 20f
        )
    }
    // Copper foil layer
    drawPath(path, thermalColor, style = Stroke(width = 8f, cap = StrokeCap.Round))
    // Kapton dielectric overlay (dashed)
    drawPath(path, Color(0xFF00E5FF).copy(alpha = 0.7f), style = Stroke(width = 12f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)))

    // Left Palmar collector pad
    drawRoundRect(
        color = coolColor,
        topLeft = Offset(20f, cy + 10f),
        size = Size(60f, 40f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(2f)
    )

    // Cold arrows moving towards palm
    val arrowX = size.width * 0.35f
    drawLine(coolColor, Offset(arrowX, cy - 10f), Offset(arrowX - 25f, cy + 10f), 2f, StrokeCap.Round)

    // Right Dorsal Mount plate
    drawRoundRect(
        color = lineColor,
        topLeft = Offset(size.width * 0.75f, cy - 35f),
        size = Size(50f, 30f),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(2f)
    )
}

private fun DrawScope.drawEnclosureDiagram() {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val lineColor = Color(0xFFE2E8F0)
    val accentColor = TechCyan

    // Outer Enclosure / Glove boundary
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(cx - 100f, cy - 45f),
        size = Size(200f, 90f),
        cornerRadius = CornerRadius(14f, 14f),
        style = Stroke(2f)
    )

    // Fan ventilation slots (left and right)
    for (i in 0..3) {
        val yOffset = cy - 25f + i * 16f
        drawLine(accentColor, Offset(cx + 65f, yOffset), Offset(cx + 85f, yOffset), 2.5f, StrokeCap.Round)
    }

    // Inner battery / PCB cradle
    drawRoundRect(
        color = lineColor,
        topLeft = Offset(cx - 85f, cy - 30f),
        size = Size(75f, 60f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(1.5f)
    )

    // USB-C port cutout
    drawRoundRect(
        color = TechAmber,
        topLeft = Offset(cx - 105f, cy - 6f),
        size = Size(12f, 20f),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(2f)
    )
}

private fun DrawScope.drawTestingVerificationDiagram() {
    val cy = size.height / 2f
    val lineColor = Color(0xFFE2E8F0)
    val meterColor = TechCyan

    // Digital Multimeter box
    val dmmX = size.width * 0.25f
    drawRoundRect(
        color = meterColor,
        topLeft = Offset(dmmX - 45f, cy - 40f),
        size = Size(90f, 80f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(2f)
    )
    // LCD Screen
    drawRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(dmmX - 35f, cy - 30f),
        size = Size(70f, 26f)
    )
    // Dial knob
    drawCircle(meterColor, 12f, Offset(dmmX, cy + 18f), style = Stroke(2f))

    // Test Probes (Red and Black)
    val redPath = Path().apply {
        moveTo(dmmX - 15f, cy + 35f)
        cubicTo(dmmX - 10f, cy + 60f, size.width * 0.55f, cy + 50f, size.width * 0.65f, cy + 5f)
    }
    drawPath(redPath, Color(0xFFEF4444), style = Stroke(2.5f, cap = StrokeCap.Round))

    val blackPath = Path().apply {
        moveTo(dmmX + 15f, cy + 35f)
        cubicTo(dmmX + 30f, cy + 65f, size.width * 0.6f, cy + 60f, size.width * 0.75f, cy + 5f)
    }
    drawPath(blackPath, Color(0xFF94A3B8), style = Stroke(2.5f, cap = StrokeCap.Round))

    // Device Under Test (Right)
    drawRect(
        color = lineColor,
        topLeft = Offset(size.width * 0.6f, cy - 30f),
        size = Size(75f, 40f),
        style = Stroke(2f)
    )
    // Test points
    drawCircle(TechAmber, 3f, Offset(size.width * 0.65f, cy + 5f))
    drawCircle(TechAmber, 3f, Offset(size.width * 0.75f, cy + 5f))
}

private fun DrawScope.drawCalloutLeader(start: Offset, bend: Offset, end: Offset, color: Color) {
    drawCircle(color, 2.5f, start)
    drawLine(color, start, bend, 1.5f)
    drawLine(color, bend, end, 1.5f)
}
