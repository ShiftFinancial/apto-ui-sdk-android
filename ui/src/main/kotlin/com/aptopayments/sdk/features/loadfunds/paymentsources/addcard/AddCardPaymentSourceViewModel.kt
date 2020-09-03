package com.aptopayments.sdk.features.loadfunds.paymentsources.addcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aptopayments.mobile.data.card.Card
import com.aptopayments.mobile.data.paymentsources.NewCard
import com.aptopayments.mobile.exception.Failure
import com.aptopayments.mobile.platform.AptoPlatformProtocol
import com.aptopayments.sdk.core.extension.toOnlyDigits
import com.aptopayments.sdk.core.platform.BaseViewModel
import com.aptopayments.sdk.features.loadfunds.paymentsources.PaymentSourcesRepository
import com.aptopayments.sdk.features.loadfunds.paymentsources.addcard.checks.CreditCardNetworkResolver
import com.aptopayments.sdk.features.loadfunds.paymentsources.addcard.states.CardNumberFieldStateResolver
import com.aptopayments.sdk.features.loadfunds.paymentsources.addcard.states.CvvFieldStateResolver
import com.aptopayments.sdk.features.loadfunds.paymentsources.addcard.states.ExpirationFieldStateResolver
import com.aptopayments.sdk.features.loadfunds.paymentsources.addcard.states.ZipFieldStateResolver
import com.aptopayments.sdk.utils.DateProvider
import com.aptopayments.sdk.utils.LiveEvent

internal class AddCardPaymentSourceViewModel(
    cardId: String,
    private val repo: PaymentSourcesRepository,
    aptoPlatform: AptoPlatformProtocol
) : BaseViewModel() {

    private val creditCardNetworkResolver = CreditCardNetworkResolver()
    private val cardNumberFieldStateResolver = CardNumberFieldStateResolver()
    private val expirationFieldStateResolver = ExpirationFieldStateResolver(DateProvider())
    private val cvvFieldStateResolver = CvvFieldStateResolver()
    private val zipFieldStateResolver = ZipFieldStateResolver()

    val creditCardNumber = MutableLiveData<String>("")
    val creditCardNetwork = Transformations.map(creditCardNumber) { processCreditCardNetwork(it.toOnlyDigits()) }

    private val creditCardState = MutableLiveData(FieldState.TYPING)
    val cardNumberError = Transformations.map(creditCardState) { it == FieldState.ERROR }
    val showAllFields = Transformations.map(creditCardState) { it == FieldState.CORRECT }

    val expiration = MutableLiveData<String>("")
    private val expirationState = Transformations.map(expiration) { expirationFieldStateResolver(it.toOnlyDigits()) }
    val expirationError = Transformations.map(expirationState) { it == FieldState.ERROR }

    val cvv = MutableLiveData<String>("")
    private val cvvState = Transformations.map(cvv) { cvvFieldStateResolver(it, creditCardNetwork.value) }

    val zipCode = MutableLiveData<String>("")
    private val zipCodeState = Transformations.map(zipCode) { zipFieldStateResolver(zipCode.value) }

    private val fieldStateList = listOf(creditCardState, expirationState, cvvState, zipCodeState)

    private val _continueButtonEnabled = MediatorLiveData<Boolean>()
    val continueButtonEnabled = _continueButtonEnabled as LiveData<Boolean>

    val cardTransactionCompleted = LiveEvent<Boolean>()

    init {
        observeAllFieldStateChanges()
        aptoPlatform.fetchFinancialAccount(cardId, false) { result ->
            result.either({ handleFailure(it) }, { setCardNetworks(it) })
        }
    }

    private fun setCardNetworks(card: Card) {
        creditCardNetworkResolver.setAllowedNetworks(card.features?.funding?.cardNetworks ?: listOf())
    }

    fun onContinueClicked() {
        showLoading()
        val expirationString = expiration.value!!
        val card = NewCard(
            description = null,
            pan = creditCardNumber.value!!.toOnlyDigits(),
            cvv = cvv.value!!,
            expirationMonth = expirationString.take(2),
            expirationYear = expirationString.drop(3),
            zipCode = zipCode.value!!
        )

        repo.addPaymentSource(card) { result ->
            hideLoading()
            result.either({ handleFailure(AddCardPaymentSourceFailure()) }, {
                cardTransactionCompleted.postValue(result.isRight)
            })
        }
    }

    private fun observeAllFieldStateChanges() {
        fieldStateList.forEach {
            _continueButtonEnabled.addSource(it) {
                _continueButtonEnabled.postValue(checkAllFieldsCorrectness())
            }
        }
    }

    private fun processCreditCardNetwork(number: String): CardNetwork {
        val network = creditCardNetworkResolver.getCardType(number)
        creditCardState.value = cardNumberFieldStateResolver(number, network)
        return network
    }

    private fun checkAllFieldsCorrectness(): Boolean {
        return fieldStateList.all { it.value != null && it.value == FieldState.CORRECT }
    }

    class AddCardPaymentSourceFailure : Failure.FeatureFailure("load_funds_add_card_error_message", "load_funds_add_card_error_title")
}
