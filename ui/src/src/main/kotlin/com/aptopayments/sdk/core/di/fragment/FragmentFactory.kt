package com.aptopayments.sdk.core.di.fragment

import com.aptopayments.core.data.card.Card
import com.aptopayments.core.data.card.KycStatus
import com.aptopayments.core.data.cardproduct.CardProduct
import com.aptopayments.core.data.config.ContextConfiguration
import com.aptopayments.core.data.config.ProjectConfiguration
import com.aptopayments.core.data.config.UITheme
import com.aptopayments.core.data.content.Content
import com.aptopayments.core.data.geo.Country
import com.aptopayments.core.data.transaction.Transaction
import com.aptopayments.core.data.user.DataPoint
import com.aptopayments.core.data.user.DataPointList
import com.aptopayments.core.data.user.Verification
import com.aptopayments.core.data.voip.Action
import com.aptopayments.core.data.workflowaction.AllowedBalanceType
import com.aptopayments.sdk.features.auth.birthdateverification.BirthdateVerificationContract
import com.aptopayments.sdk.features.auth.inputemail.InputEmailContract
import com.aptopayments.sdk.features.auth.inputphone.InputPhoneContract
import com.aptopayments.sdk.features.auth.verification.EmailVerificationContract
import com.aptopayments.sdk.features.auth.verification.PhoneVerificationContract
import com.aptopayments.sdk.features.card.account.AccountSettingsContract
import com.aptopayments.sdk.features.card.activatephysicalcard.activate.ActivatePhysicalCardContract
import com.aptopayments.sdk.features.card.activatephysicalcard.success.ActivatePhysicalCardSuccessContract
import com.aptopayments.sdk.features.card.cardsettings.CardSettingsContract
import com.aptopayments.sdk.features.card.cardstats.CardMonthlyStatsContract
import com.aptopayments.sdk.features.card.cardstats.chart.CardTransactionsChartContract
import com.aptopayments.sdk.features.card.fundingsources.FundingSourceContract
import com.aptopayments.sdk.features.card.notificationpreferences.NotificationPreferencesContract
import com.aptopayments.sdk.features.card.setpin.ConfirmPinContract
import com.aptopayments.sdk.features.card.setpin.SetPinContract
import com.aptopayments.sdk.features.card.statements.StatementListContract
import com.aptopayments.sdk.features.card.transactionlist.TransactionListConfig
import com.aptopayments.sdk.features.card.transactionlist.TransactionListContract
import com.aptopayments.sdk.features.card.waitlist.WaitlistContract
import com.aptopayments.sdk.features.contentpresenter.ContentPresenterContract
import com.aptopayments.sdk.features.disclaimer.DisclaimerContract
import com.aptopayments.sdk.features.issuecard.IssueCardContract
import com.aptopayments.sdk.features.issuecard.IssueCardErrorContract
import com.aptopayments.sdk.features.kyc.KycStatusContract
import com.aptopayments.sdk.features.maintenance.MaintenanceContract
import com.aptopayments.sdk.features.managecard.ManageCardContract
import com.aptopayments.sdk.features.nonetwork.NoNetworkContract
import com.aptopayments.sdk.features.oauth.OAuthConfig
import com.aptopayments.sdk.features.oauth.connect.OAuthConnectContract
import com.aptopayments.sdk.features.oauth.verify.OAuthVerifyContract
import com.aptopayments.sdk.features.pin.CreatePinContract
import com.aptopayments.sdk.features.selectcountry.CountrySelectorContract
import com.aptopayments.sdk.features.transactiondetails.TransactionDetailsContract
import com.aptopayments.sdk.features.voip.VoipContract
import com.aptopayments.sdk.ui.fragments.pdf.PdfRendererContract
import com.aptopayments.sdk.ui.fragments.webbrowser.WebBrowserContract
import org.threeten.bp.LocalDate
import java.io.File

internal interface FragmentFactory {

    fun countrySelectorFragment(
            uiTheme: UITheme,
            allowedCountries: List<Country>,
            tag: String
    ): CountrySelectorContract.View
    fun inputPhoneFragment(
            uiTheme: UITheme,
            allowedCountries: List<Country>,
            tag: String
    ): InputPhoneContract.View
    fun inputEmailFragment(
            uiTheme: UITheme,
            tag: String
    ): InputEmailContract.View
    fun phoneVerificationFragment(
            uiTheme: UITheme,
            verification: Verification,
            tag: String
    ): PhoneVerificationContract.View
    fun emailVerificationFragment(
            uiTheme: UITheme,
            verification: Verification,
            tag: String
    ): EmailVerificationContract.View
    fun birthdateVerificationFragment(
            uiTheme: UITheme,
            primaryCredential: DataPoint,
            tag: String
    ): BirthdateVerificationContract.View
    fun kycStatusFragment(
            uiTheme: UITheme,
            kycStatus: KycStatus,
            cardID: String,
            tag: String
    ): KycStatusContract.View
    fun noNetworkFragment(
            uiTheme: UITheme,
            tag: String
    ): NoNetworkContract.View
    fun maintenanceFragment(
            uiTheme: UITheme,
            tag: String
    ): MaintenanceContract.View
    fun disclaimerFragment(
            uiTheme: UITheme,
            content: Content,
            tag: String
    ): DisclaimerContract.View
    fun oauthConnectFragment(
            uiTheme: UITheme,
            config: OAuthConfig,
            tag: String
    ): OAuthConnectContract.View
    fun contentPresenterFragment(
            uiTheme: UITheme,
            content: Content,
            title: String,
            tag: String
    ): ContentPresenterContract.View
    fun oauthVerifyFragment(
            uiTheme: UITheme,
            datapoints: DataPointList,
            allowedBalanceType: AllowedBalanceType,
            tokenId: String,
            tag: String
    ): OAuthVerifyContract.View
    fun manageCardFragment(
            uiTheme: UITheme,
            cardId: String,
            tag: String
    ): ManageCardContract.View
    fun fundingSourceFragment(
            uiTheme: UITheme,
            cardID: String,
            selectedBalanceID: String?,
            tag: String
    ): FundingSourceContract.View
    fun accountSettingsFragment(
            uiTheme: UITheme,
            contextConfiguration: ContextConfiguration,
            tag: String
    ): AccountSettingsContract.View
    fun activatePhysicalCardFragment(
            uiTheme: UITheme,
            card: Card,
            tag: String
    ): ActivatePhysicalCardContract.View
    fun activatePhysicalCardSuccessFragment(
            uiTheme: UITheme,
            card: Card,
            tag: String
    ): ActivatePhysicalCardSuccessContract.View
    fun cardSettingsFragment(
            uiTheme: UITheme,
            card: Card,
            cardProduct: CardProduct,
            projectConfiguration: ProjectConfiguration,
            tag: String
    ): CardSettingsContract.View
    fun transactionDetailsFragment(
            uiTheme: UITheme,
            transaction: Transaction,
            tag: String
    ): TransactionDetailsContract.View
    fun cardMonthlyStatsFragment(
            uiTheme: UITheme,
            cardId: String,
            tag: String
    ): CardMonthlyStatsContract.View
    fun cardTransactionsChartFragment(
            uiTheme: UITheme,
            cardId: String,
            date: LocalDate,
            tag: String
    ): CardTransactionsChartContract.View
    fun webBrowserFragment(
            url: String,
            tag: String
    ): WebBrowserContract.View
    fun notificationPreferencesFragment(
            uiTheme: UITheme,
            cardId: String,
            tag: String
    ): NotificationPreferencesContract.View
    fun issueCardFragment(
            uiTheme: UITheme,
            tag: String,
            cardApplicationId: String
    ): IssueCardContract.View
    fun issueCardErrorFragment(
            uiTheme: UITheme,
            tag: String,
            errorCode: Int?,
            errorAsset: String?
    ): IssueCardErrorContract.View
    fun transactionListFragment(
            uiTheme: UITheme,
            cardId: String,
            config: TransactionListConfig,
            tag: String
    ): TransactionListContract.View
    fun waitlistFragment(
            uiTheme: UITheme,
            cardId: String,
            cardProduct: CardProduct,
            tag: String
    ): WaitlistContract.View
    fun setPinFragment(
            uiTheme: UITheme,
            tag: String
    ): SetPinContract.View
    fun confirmPinFragment(
            uiTheme: UITheme,
            pin: String,
            tag: String
    ): ConfirmPinContract.View
    fun getVoipFragment(
            uiTheme: UITheme,
            cardId: String,
            action: Action,
            tag: String
    ): VoipContract.View

    fun statementListFragment(
        uiTheme: UITheme,
        tag: String
    ): StatementListContract.View

    fun pdfRendererFragment(
        uiTheme: UITheme,
        title: String, file: File, tag: String
    ): PdfRendererContract.View

    fun createPinFragment(uiTheme: UITheme, tag: String): CreatePinContract.View
}
