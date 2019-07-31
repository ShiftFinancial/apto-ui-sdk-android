package com.aptopayments.sdk.features.kyc

import androidx.lifecycle.MutableLiveData
import com.aptopayments.core.data.card.Card
import com.aptopayments.core.data.card.KycStatus
import com.aptopayments.core.platform.AptoPlatform
import com.aptopayments.sdk.core.platform.BaseViewModel
import com.aptopayments.sdk.features.analytics.AnalyticsServiceContract
import com.aptopayments.core.analytics.Event
import javax.inject.Inject

internal class KycStatusViewModel
@Inject constructor(
        private val analyticsManager: AnalyticsServiceContract
) : BaseViewModel() {

    var kycStatus: MutableLiveData<KycStatus> = MutableLiveData()

    fun getKycStatus(cardID: String) {
        AptoPlatform.fetchFinancialAccount(accountId = cardID, showDetails = false, forceRefresh = true) { result ->
            result.either(::handleFailure) {
                handleCard(it)
            }
        }
    }

    private fun handleCard(card: Card) {
        kycStatus.postValue(card.kycStatus)
    }

    fun viewLoaded() {
        analyticsManager.track(Event.ManageCardKycStatus)
    }
}
