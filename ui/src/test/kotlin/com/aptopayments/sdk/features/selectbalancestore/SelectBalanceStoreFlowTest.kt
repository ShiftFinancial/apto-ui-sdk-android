package com.aptopayments.sdk.features.selectbalancestore

import com.aptopayments.mobile.analytics.Event
import com.aptopayments.mobile.data.card.SelectBalanceStoreResult
import com.aptopayments.mobile.data.config.UIConfig
import com.aptopayments.mobile.data.workflowaction.WorkflowActionConfigurationSelectBalanceStore
import com.aptopayments.mobile.exception.Failure
import com.aptopayments.mobile.functional.Either
import com.aptopayments.mobile.platform.AptoPlatform
import com.aptopayments.mobile.platform.AptoPlatformProtocol
import com.aptopayments.sdk.UnitTest
import com.aptopayments.sdk.core.data.TestDataProvider
import com.aptopayments.sdk.core.di.fragment.FragmentFactory
import com.aptopayments.sdk.features.analytics.AnalyticsServiceContract
import com.aptopayments.sdk.features.common.analytics.AnalyticsManagerSpy
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectBalanceStoreFlowTest : UnitTest() {

    private lateinit var sut: SelectBalanceStoreFlow
    @Mock
    private lateinit var mockFragmentFactory: FragmentFactory
    private val cardApplicationId = "TEST_CARD_APPLICATION_ID"

    private var analyticsManager: AnalyticsManagerSpy = AnalyticsManagerSpy()
    @Mock
    private lateinit var mockAptoPlatform: AptoPlatform

    @Before
    fun setUp() {
        UIConfig.updateUIConfigFrom(TestDataProvider.provideProjectBranding())
        startKoin {
            modules(
                module {
                    single { mockFragmentFactory }
                    single<AnalyticsServiceContract> { analyticsManager }
                    single<AptoPlatformProtocol> { mockAptoPlatform }
                }
            )
        }
        val testAllowedBalanceType = TestDataProvider.provideAllowedBalanceType()
        val testAllowedBalanceTypeList = listOf(testAllowedBalanceType)
        val mockActionConfig = WorkflowActionConfigurationSelectBalanceStore(testAllowedBalanceTypeList, null)
        sut = SelectBalanceStoreFlow(
            actionConfiguration = mockActionConfig,
            cardApplicationId = cardApplicationId, onBack = {}, onFinish = {}
        )
    }

    @Test
    fun `should start the OAuth flow if config contains an allowed balance type`() {
        // Given
        val sutSpy = spy(sut)
        doNothing().whenever(sutSpy).initOAuthFlow(
            TestDataProvider.anyObject(), TestDataProvider.anyObject(),
            TestDataProvider.anyObject()
        )

        // When
        sutSpy.init {}

        // Then
        verify(sutSpy).initOAuthFlow(
            TestDataProvider.anyObject(), TestDataProvider.anyObject(),
            TestDataProvider.anyObject()
        )
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `should track event selectBalanceStoreIdentityNotVerified when select balance store fails`() {
        // Given
        val result = SelectBalanceStoreResult(
            result = SelectBalanceStoreResult.Type.INVALID,
            errorCode = 200046
        )
        whenever(
            mockAptoPlatform.setBalanceStore(
                anyString(),
                anyString(),
                TestDataProvider.anyObject()
            )
        ).thenAnswer { invocation ->
            (invocation.arguments[2] as (Either<Failure, SelectBalanceStoreResult>) -> Unit).invoke(Either.Right(result))
        }

        // When
        sut.selectBalanceStore(TestDataProvider.provideOAuthAttempt())

        // Then
        assertTrue(analyticsManager.trackCalled)
        assertEquals(analyticsManager.lastEvent, Event.SelectBalanceStoreOauthConfirmIdentityNotVerified)
    }
}
