package com.aptopayments.sdk.features.card.transactionlist

import com.aptopayments.core.data.config.UIConfig
import com.aptopayments.core.data.config.UITheme.THEME_2
import com.aptopayments.core.data.transaction.MCC
import com.aptopayments.core.data.transaction.Transaction
import com.aptopayments.sdk.AndroidTest
import com.aptopayments.sdk.core.data.TestDataProvider
import com.aptopayments.sdk.core.di.fragment.FragmentFactory
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.mockito.Mock
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionListFlowTest : AndroidTest() {
    private lateinit var sut: TransactionListFlow
    // Collaborators
    @Mock private lateinit var mockFragmentFactory: FragmentFactory
    private val cardId = "cardId"
    private val config = TransactionListConfig(startDate = null, endDate = null, mcc = MCC(name = null, icon = null))
    private val tag = "TransactionListFragment"
    private val detailsTag = "TransactionDetailsFragment"

    @Before
    override fun setUp() {
        super.setUp()
        UIConfig.updateUIConfigFrom(TestDataProvider.provideProjectBranding())
        startKoin {
            modules(module {
                single{ mockFragmentFactory }
            })
        }
    }

    @Test
    fun `sut initialized with THEME2 instantiate fragment from the factory`() {
        // Given
        sut = TransactionListFlow(cardId = cardId, config = config, onBack = {})
        UIConfig.uiTheme = THEME_2
        val fragmentTestDouble = transactionListFragmentTestDouble()
        given {
            mockFragmentFactory.transactionListFragment(uiTheme = THEME_2, cardId = cardId, config = config, tag = tag)
        }.willReturn(fragmentTestDouble)

        // When
        sut.init {}

        // Then
        verify(mockFragmentFactory).transactionListFragment(uiTheme = THEME_2, cardId = cardId, config = config, tag = tag)
        assertEquals(sut, fragmentTestDouble.delegate)
    }

    @Test
    fun `on back pressed in fragment call on back closure`() {
        // Given
        var onBackCalled = false
        sut = TransactionListFlow(cardId = cardId, config = config, onBack = { onBackCalled = true })

        // When
        sut.onBackPressed()

        // Then
        assertTrue { onBackCalled }
    }

    @Test
    fun `on transaction tapped with THEME2 instantiate fragment from the factory`() {
        // Given
        sut = TransactionListFlow(cardId = cardId, config = config, onBack = {})
        UIConfig.uiTheme = THEME_2
        val transaction = mock(Transaction::class.java)
        val fragmentTestDouble = transactionDetailsFragmentTestDouble()
        given {
            mockFragmentFactory.transactionDetailsFragment(THEME_2, transaction, detailsTag)
        }.willReturn(fragmentTestDouble)

        // When
        sut.onTransactionTapped(transaction)

        // Then
        verify(mockFragmentFactory).transactionDetailsFragment(THEME_2, transaction, detailsTag)
        assertEquals(sut, fragmentTestDouble.delegate)
    }

    private fun transactionListFragmentTestDouble(): TransactionListFragmentTestDouble {
        val fragmentTestDouble = TransactionListFragmentTestDouble()
        fragmentTestDouble.TAG = tag
        return fragmentTestDouble
    }

    private fun transactionDetailsFragmentTestDouble(): TransactionDetailsFragmentTestDouble {
        val fragmentTestDouble = TransactionDetailsFragmentTestDouble()
        fragmentTestDouble.TAG = detailsTag
        return fragmentTestDouble
    }
}
