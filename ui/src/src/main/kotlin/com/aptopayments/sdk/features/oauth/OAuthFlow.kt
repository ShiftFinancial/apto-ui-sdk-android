package com.aptopayments.sdk.features.oauth

import androidx.annotation.VisibleForTesting
import com.aptopayments.core.data.config.UIConfig
import com.aptopayments.core.data.oauth.OAuthAttempt
import com.aptopayments.core.data.workflowaction.AllowedBalanceType
import com.aptopayments.core.exception.Failure
import com.aptopayments.core.functional.Either
import com.aptopayments.sdk.core.platform.BaseFragment
import com.aptopayments.sdk.core.platform.flow.Flow
import com.aptopayments.sdk.core.platform.flow.FlowPresentable
import com.aptopayments.sdk.features.oauth.connect.OAuthConnectContract
import com.aptopayments.core.repository.oauth.remote.OAUTH_FINISHED_URL
import com.aptopayments.sdk.ui.fragments.webbrowser.WebBrowserContract
import java.lang.reflect.Modifier

private const val OAUTH_CONNECT_TAG = "OAuthConnectFragment"
private const val WEB_BROWSER_TAG = "WebBrowserFragment"

@VisibleForTesting(otherwise = Modifier.PROTECTED)
internal class OAuthFlow(
        var allowedBalanceType: AllowedBalanceType,
        var onBack: (Unit) -> Unit,
        var onFinish: (oauthAttempt: OAuthAttempt) -> Unit
) : Flow(), OAuthConnectContract.Delegate, WebBrowserContract.Delegate {

    override fun init(onInitComplete: (Either<Failure, Unit>) -> Unit) {
        appComponent.inject(this)
        val fragment = fragmentFactory.oauthConnectFragment(
                uiTheme = UIConfig.uiTheme,
                allowedBalanceType = allowedBalanceType,
                tag = OAUTH_CONNECT_TAG)
        fragment.delegate = this
        setStartElement(element = fragment as FlowPresentable)
        onInitComplete(Either.Right(Unit))
    }

    override fun restoreState() {
        (fragmentWithTag(OAUTH_CONNECT_TAG) as? OAuthConnectContract.View)?.let {
            it.delegate = this
        }
        (fragmentWithTag(WEB_BROWSER_TAG) as? WebBrowserContract.View)?.let {
            it.delegate = this
        }
    }

    //
    // OAuthConnect
    //
    override fun onBackFromOAuthConnect() {
        onBack(Unit)
    }

    override fun show(url: String) {
        val fragment = fragmentFactory.webBrowserFragment(url = url, tag = WEB_BROWSER_TAG)
        fragment.delegate = this
        push(fragment = fragment as BaseFragment)
    }

    override fun onOAuthSuccess(oauthAttempt: OAuthAttempt) {
        onFinish(oauthAttempt)
    }

    //
    // WebBrowserContract handling
    //

    override fun onCloseWebBrowser() {
        popFragment()
        (startFragment() as? OAuthConnectContract.View)?.reloadStatus()
    }

    override fun shouldStopLoadingAndClose(url: String): Boolean {
        return url.startsWith(OAUTH_FINISHED_URL, ignoreCase = true)
    }
}
