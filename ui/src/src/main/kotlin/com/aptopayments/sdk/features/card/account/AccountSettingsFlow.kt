package com.aptopayments.sdk.features.card.account


import androidx.annotation.VisibleForTesting
import com.aptopayments.core.data.config.ContextConfiguration
import com.aptopayments.core.data.config.UIConfig
import com.aptopayments.core.exception.Failure
import com.aptopayments.core.functional.Either
import com.aptopayments.core.repository.UserSessionRepository
import com.aptopayments.sdk.core.platform.BaseFragment
import com.aptopayments.sdk.core.platform.flow.Flow
import com.aptopayments.sdk.core.platform.flow.FlowPresentable
import com.aptopayments.sdk.features.analytics.AnalyticsServiceContract
import com.aptopayments.sdk.features.card.notificationpreferences.NotificationPreferencesContract
import java.lang.reflect.Modifier
import javax.inject.Inject

private const val ACCOUNT_SETTINGS_TAG = "AccountSettingsFragment"
private const val NOTIFICATION_PREFERENCES_TAG = "NotificationPreferencesFragment"

@VisibleForTesting(otherwise = Modifier.PROTECTED)
internal class AccountSettingsFlow(
        private val cardId: String,
        private val contextConfiguration: ContextConfiguration,
        private var onClose: () -> Unit
) : Flow(), AccountSettingsContract.Delegate, NotificationPreferencesContract.Delegate {

    @Inject lateinit var userSessionRepository: UserSessionRepository
    @Inject lateinit var analyticsManager: AnalyticsServiceContract

    override fun init(onInitComplete: (Either<Failure, Unit>) -> Unit) {
        appComponent.inject(this)
        val fragment = fragmentFactory.accountSettingsFragment(
                UIConfig.uiTheme,
                contextConfiguration,
                ACCOUNT_SETTINGS_TAG)
        fragment.delegate = this
        setStartElement(element = fragment as FlowPresentable)
        onInitComplete(Either.Right(Unit))
    }

    override fun restoreState() {
        (fragmentWithTag(ACCOUNT_SETTINGS_TAG) as? AccountSettingsContract.View)?.let {
            it.delegate = this
        }
        (fragmentWithTag(NOTIFICATION_PREFERENCES_TAG) as? NotificationPreferencesContract.View)?.let {
            it.delegate = this
        }
    }

    override fun onAccountSettingsClosed() = onClose()

    override fun onLogOut() {
        userSessionRepository.clearUserSession()
        analyticsManager.logoutUser()
    }

    //
    // Notification Preferences
    //
    override fun showNotificationPreferences() {
        val fragment = fragmentFactory.notificationPreferencesFragment(
                UIConfig.uiTheme, cardId, NOTIFICATION_PREFERENCES_TAG)
        fragment.delegate = this
        push(fragment as BaseFragment)
    }

    override fun onBackFromNotificationsPreferences() = popFragment()
}
