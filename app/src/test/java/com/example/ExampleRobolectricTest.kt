package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.DeviceGuideRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Build What You See", appName)
    }

    @Test
    fun `verify peltier cooling glove guide generation`() {
        val guide = DeviceGuideRepository.getPeltierCoolingGloveGuide()
        assertNotNull(guide)
        assertEquals("Active Peltier Palm Cooling & Hyperhidrosis Glove", guide.deviceTitle)
        assertTrue(guide.components.isNotEmpty())
        assertTrue(guide.assemblySteps.isNotEmpty())
        assertTrue(guide.savingsPercentage > 85)
        
        val markdownTable = guide.toMarkdownTable()
        assertTrue(markdownTable.contains("Component Name"))
        assertTrue(markdownTable.contains("Estimated Unit Price (USD)"))
        assertTrue(markdownTable.contains("TOTAL ESTIMATED BUILD COST"))
    }

    @Test
    fun `verify iontophoresis medical device guide generation`() {
        val guide = DeviceGuideRepository.getIontophoresisDeviceGuide()
        assertNotNull(guide)
        assertEquals("Medical-Grade Iontophoresis Pulse Controller", guide.deviceTitle)
        assertTrue(guide.totalBuildCostUsd < 50.0)
        assertTrue(guide.commercialPriceUsd >= 400.0)
        assertTrue(guide.savingsPercentage >= 90)
    }

    @Test
    fun `verify offline custom device analyzer`() {
        val guide = DeviceGuideRepository.analyzeCustomDeviceOffline("Peltier bio-cooler with heat sink")
        assertNotNull(guide)
        assertTrue(guide.components.isNotEmpty())
        assertTrue(guide.assemblySteps.isNotEmpty())
    }
}
