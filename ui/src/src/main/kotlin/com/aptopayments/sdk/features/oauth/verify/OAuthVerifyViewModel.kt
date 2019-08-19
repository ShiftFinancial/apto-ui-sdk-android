package com.aptopayments.sdk.features.oauth.verify

import androidx.lifecycle.MutableLiveData
import com.aptopayments.core.analytics.Event
import com.aptopayments.core.data.oauth.OAuthUserDataUpdate
import com.aptopayments.core.data.user.*
import com.aptopayments.core.data.workflowaction.AllowedBalanceType
import com.aptopayments.core.platform.AptoPlatform
import com.aptopayments.sdk.core.platform.BaseViewModel
import com.aptopayments.sdk.features.analytics.AnalyticsServiceContract

internal class OAuthVerifyViewModel constructor(
        private val analyticsManager: AnalyticsServiceContract
) : BaseViewModel() {

    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var address: MutableLiveData<String> = MutableLiveData()
    var birthdate: MutableLiveData<String> = MutableLiveData()

    fun setDataPoints(datapointList: DataPointList) {
        (datapointList.getUniqueDataPointOf(DataPoint.Type.NAME, null) as? NameDataPoint)?.let {
            firstName.value = it.firstName
            lastName.value = it.lastName
        }
        (datapointList.getUniqueDataPointOf(DataPoint.Type.EMAIL, null) as? EmailDataPoint)?.let {
            email.value = it.email
        }
        (datapointList.getUniqueDataPointOf(DataPoint.Type.PHONE, null) as? PhoneDataPoint)?.let {
            phone.value = it.phoneNumber.toStringRepresentation()
        }
        (datapointList.getUniqueDataPointOf(DataPoint.Type.ADDRESS, null) as? AddressDataPoint)?.let {
            address.value = it.toStringRepresentation()
        }
        (datapointList.getUniqueDataPointOf(DataPoint.Type.BIRTHDATE, null) as? BirthdateDataPoint)?.let {
            birthdate.value = it.toStringRepresentation()
        }
    }

    fun retrieveUpdatedUserData(allowedBalanceType: AllowedBalanceType, tokenId: String, callback: (oauthAttempt: OAuthUserDataUpdate) -> Unit) {
        analyticsManager.track(Event.SelectBalanceStoreOauthConfirmRefreshDetailsTap)
        AptoPlatform.fetchOAuthData(allowedBalanceType, tokenId) { result ->
            result.either(::handleFailure) {
                it.userData?.let { dataPoints -> setDataPoints(dataPoints) }
                callback(it)
            }
        }
    }

    fun viewLoaded() {
        analyticsManager.track(Event.SelectBalanceStoreOauthConfirm)
    }
}
