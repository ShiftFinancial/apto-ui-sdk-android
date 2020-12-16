package com.aptopayments.sdk.features.verification

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aptopayments.mobile.analytics.Event
import com.aptopayments.mobile.data.user.DataPoint
import com.aptopayments.mobile.data.user.Verification
import com.aptopayments.mobile.data.user.VerificationStatus
import com.aptopayments.sdk.UnitTest
import com.aptopayments.sdk.features.auth.verification.VerificationViewModel
import com.aptopayments.sdk.features.common.analytics.AnalyticsManagerSpy
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VerificationViewModelTest : UnitTest() {
    private lateinit var verificationViewModel: VerificationViewModel

    private var analyticsManager: AnalyticsManagerSpy = AnalyticsManagerSpy()

    @Mock
    private lateinit var verificationLiveData: MutableLiveData<Verification>

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun `set up for testing`() {
        verificationViewModel = VerificationViewModel(analyticsManager)
    }

    @Test
    fun `test for verification with non-pending status`() {
        val verification = Verification("verificationId", "email", VerificationStatus.FAILED)

        verificationViewModel.finishVerification(verification.verificationId) { result -> result.isLeft }
        verify(verificationLiveData, never()).postValue(verification)
    }

    @Test
    fun `test track is called on view loaded`() {
        verificationViewModel.viewLoaded(DataPoint.Type.EMAIL)
        assertTrue { analyticsManager.trackCalled }
        assertEquals(analyticsManager.lastEvent, Event.AuthVerifyEmail)
    }
}
