package com.aptopayments.sdk.features.oauth.connect

import com.aptopayments.core.analytics.Event
import com.aptopayments.sdk.AndroidTest
import com.aptopayments.sdk.features.common.analytics.AnalyticsManagerSpy
import org.junit.Before
import org.junit.Test
import org.mockito.Spy
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OAuthConnectViewModelTest : AndroidTest() {

    private lateinit var sut: OAuthConnectViewModel
    @Spy private var analyticsManager: AnalyticsManagerSpy = AnalyticsManagerSpy()

    @Before
    override fun setUp() {
        super.setUp()
        sut = OAuthConnectViewModel(analyticsManager)
    }

    @Test
    fun `test track is called on view loaded`() {
        sut.viewLoaded()
        assertTrue { analyticsManager.trackCalled }
        assertEquals(analyticsManager.lastEvent, Event.SelectBalanceStoreOauthLogin)
    }
}
