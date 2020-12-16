package com.aptopayments.sdk.features.card.setpin

import com.aptopayments.mobile.data.config.UIConfig
import com.aptopayments.sdk.R
import com.aptopayments.sdk.core.extension.ToolbarConfiguration
import com.aptopayments.sdk.core.extension.configure
import com.aptopayments.sdk.core.extension.observe
import com.aptopayments.sdk.core.platform.BaseFragment
import com.aptopayments.sdk.core.platform.theme.themeManager
import com.aptopayments.sdk.utils.TextInputWatcher
import com.aptopayments.sdk.utils.ValidInputListener
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_set_pin.*
import kotlinx.android.synthetic.main.include_toolbar_two.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val PIN_CHARACTERS = 4

internal class SetCardPinFragment : BaseFragment(), SetCardPinContract.View {

    private val viewModel: SetCardPinViewModel by viewModel()
    override var delegate: SetCardPinContract.Delegate? = null

    override fun layoutId(): Int = R.layout.fragment_set_pin

    override fun backgroundColor(): Int = UIConfig.uiBackgroundPrimaryColor

    override fun setupViewModel() {
        viewModel.apply {
            observe(failure) { handleFailure(it) }
        }
    }

    override fun onPresented() {
        customizePrimaryNavigationStatusBar()
        pin_view.text?.clear()
        pin_view.requestFocus()
        showKeyboard()
    }

    override fun setupUI() {
        setupTexts()
        setupTheme()
        setupToolBar()
    }

    private fun setupTexts() {
        tv_set_pin_title.localizedText = "manage_card.set_pin.title"
        tv_set_pin_explanation.localizedText = "manage_card.set_pin.explanation"
    }

    private fun setupTheme() {
        with(themeManager()) {
            customizeSecondaryNavigationToolBar(tb_llsdk_toolbar_layout as AppBarLayout)
            customizeLargeTitleLabel(tv_set_pin_title)
            customizeRegularTextLabel(tv_set_pin_explanation)
        }
    }

    private fun setupToolBar() = tb_llsdk_toolbar.configure(this, ToolbarConfiguration.Builder().build())

    override fun setupListeners() {
        super.setupListeners()
        val validator = object : ValidInputListener {
            override fun onValidInput(isValid: Boolean) {
                if (isValid) delegate?.setPinFinished(pin_view.text.toString())
            }
        }
        pin_view.addTextChangedListener(TextInputWatcher(validator, PIN_CHARACTERS, pin_view))
    }

    override fun onBackPressed() {
        hideKeyboard()
        delegate?.onCloseFromSetPin()
    }

    override fun viewLoaded() = viewModel.viewLoaded()
}
