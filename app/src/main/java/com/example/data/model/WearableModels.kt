package com.example.data.model

data class ComponentItem(
    val id: String,
    val name: String,
    val specificMaterialOrModel: String,
    val estimatedPriceUsd: Double,
    val whereToBuy: String,
    val category: ComponentCategory = ComponentCategory.ELECTRONICS,
    val notes: String = "",
    val imageDrawableRes: Int? = null,
    val amazonPriceUsd: Double = estimatedPriceUsd,
    val amazonSearchQuery: String = ""
) {
    val amazonUrl: String
        get() {
            val query = if (amazonSearchQuery.isNotBlank()) amazonSearchQuery else "$name $specificMaterialOrModel"
            return "https://www.amazon.com/s?k=" + java.net.URLEncoder.encode(query, "UTF-8")
        }
}

enum class ComponentCategory(val label: String) {
    THERMAL("Thermal Management"),
    ELECTRONICS("Power & Logic"),
    CHASSIS("Structural / Wearable"),
    ELECTRODES("Electrodes / Contact"),
    FASTENERS("Fasteners & Wiring")
}

data class AssemblyStep(
    val stepNumber: Int,
    val title: String,
    val punchyInstructions: List<String>,
    val aiImagePromptLabel: String,
    val electricalSafetyProtocol: String,
    val heatManagementProtocol: String,
    val diagramType: DiagramType = DiagramType.WIRING_SCHEMATIC,
    val requiredTools: List<String> = emptyList(),
    var isCompleted: Boolean = false
)

enum class DiagramType {
    EXPLODED_VIEW,
    WIRING_SCHEMATIC,
    THERMAL_BUS_INTERFACE,
    INSULATION_AND_ENCLOSURE,
    TESTING_AND_VERIFICATION
}

data class ProductionGuide(
    val id: String,
    val deviceTitle: String,
    val deviceCategory: String,
    val subtitle: String,
    val commercialAlternativeName: String,
    val commercialPriceUsd: Double,
    val totalBuildCostUsd: Double,
    val estimatedBuildTimeMinutes: Int,
    val difficultyLevel: String, // Beginner, Intermediate, Advanced
    val imageDrawableRes: Int? = null,
    val customImageUri: String? = null,
    val components: List<ComponentItem>,
    val assemblySteps: List<AssemblyStep>,
    val safetyGuidelinesSummary: List<String>,
    val testingChecklist: List<String>
) {
    val savingsUsd: Double get() = (commercialPriceUsd - totalBuildCostUsd).coerceAtLeast(0.0)
    val savingsPercentage: Int get() = if (commercialPriceUsd > 0) {
        (((commercialPriceUsd - totalBuildCostUsd) / commercialPriceUsd) * 100).toInt()
    } else 0

    fun toMarkdownTable(): String {
        val sb = StringBuilder()
        sb.append("| Component Name | Specific Material/Model | Estimated Unit Price (USD) | Where To Buy (Retailers) |\n")
        sb.append("| :--- | :--- | :--- | :--- |\n")
        components.forEach { comp ->
            sb.append("| ${comp.name} | ${comp.specificMaterialOrModel} | $${String.format("%.2f", comp.estimatedPriceUsd)} | ${comp.whereToBuy} |\n")
        }
        sb.append("| **TOTAL ESTIMATED BUILD COST** | **All Components Combined** | **$${String.format("%.2f", totalBuildCostUsd)}** | **Multi-vendor Sourcing** |\n")
        return sb.toString()
    }

    fun toFullMarkdown(): String {
        val sb = StringBuilder()
        sb.append("# ${deviceTitle}\n\n")
        sb.append("## Executive Cost Slashing Summary\n")
        sb.append("- **Commercial Medical/Tech Alternative:** ${commercialAlternativeName} (~$${String.format("%.2f", commercialPriceUsd)})\n")
        sb.append("- **Total DIY Build Cost:** $${String.format("%.2f", totalBuildCostUsd)}\n")
        sb.append("- **Direct Cost Savings:** $${String.format("%.2f", savingsUsd)} (${savingsPercentage}% Saved)\n")
        sb.append("- **Assembly Time:** ~${estimatedBuildTimeMinutes} minutes | **Difficulty:** ${difficultyLevel}\n\n")

        sb.append("## STEP 1: MATERIAL & SOURCING LIST\n\n")
        sb.append(toMarkdownTable())
        sb.append("\n\n")

        sb.append("## STEP 2: ILLUSTRATED PRODUCTION MANUAL\n\n")
        assemblySteps.forEach { step ->
            sb.append("### Step ${step.stepNumber}: ${step.title}\n\n")
            step.punchyInstructions.forEach { instruction ->
                sb.append("- ${instruction}\n")
            }
            sb.append("\n")
            sb.append("**AI Image Generator Prompt:**\n")
            sb.append("> `${step.aiImagePromptLabel}`\n\n")
            sb.append("**⚡ Electrical Safety Protocol:** ${step.electricalSafetyProtocol}\n\n")
            sb.append("**🌡️ Heat Management Protocol:** ${step.heatManagementProtocol}\n\n")
            sb.append("---\n\n")
        }

        sb.append("## Commissioning & Electrical Safety Checklist\n")
        testingChecklist.forEach { check ->
            sb.append("- [ ] ${check}\n")
        }
        sb.append("\n")
        return sb.toString()
    }
}
