package com.aptopayments.sdk.features.loadfunds.paymentsources.onboarding

import android.os.Bundle
import android.view.View
import com.aptopayments.mobile.data.config.UIConfig
import com.aptopayments.sdk.R
import com.aptopayments.sdk.core.extension.BackButtonMode
import com.aptopayments.sdk.core.extension.ToolbarConfiguration
import com.aptopayments.sdk.core.extension.configure
import com.aptopayments.sdk.core.extension.observeNotNullable
import com.aptopayments.sdk.core.platform.BaseBindingFragment
import com.aptopayments.sdk.core.platform.theme.themeManager
import com.aptopayments.sdk.databinding.FragmentAddCardOnboardingBinding
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.include_toolbar_two.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val CARD_ID_KEY = "CARD_ID_KEY"

internal class AddCardOnboardingFragment : BaseBindingFragment<FragmentAddCardOnboardingBinding>(),
    AddCardOnboardingContract.View {

    override var delegate: AddCardOnboardingContract.Delegate? = null
    private val viewModel: AddCardOnboardingViewModel by viewModel { parametersOf(cardId) }
    private lateinit var cardId: String

    override fun layoutId() = R.layout.fragment_add_card_onboarding

    override fun backgroundColor() = UIConfig.uiBackgroundSecondaryColor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }

    override fun setUpArguments() {
        super.setUpArguments()
        cardId = arguments!![CARD_ID_KEY] as String
    }

    override fun setupViewModel() {
        observeNotNullable(viewModel.continueEvent) { delegate?.onContinueAddCardOnboarding() }
        observeNotNullable(viewModel.loading) { handleLoading(it) }
        observeNotNullable(viewModel.failure) { handleFailure(it) }
    }

    override fun onBackPressed() {
        delegate?.onBackAddCardOnboarding()
    }

    override fun setupUI() {
        themeManager().customizeSubmitButton(binding.addCardFtuContinue)
        with(themeManager()) {
            customizeSecondaryNavigationToolBar(binding.tbLlsdkToolbarLayout as AppBarLayout)
            customizeLargeTitleLabel(binding.addCardOnboardingTitle)
            customizeFormLabel(binding.addCardOnboardingDescriptionFirst)
            customizeFormLabel(binding.addCardOnboardingDescriptionSecond)
            customizeSubmitButton(binding.addCardFtuContinue)
        }

        setupToolBar()
    }

    private fun setupToolBar() {
        tb_llsdk_toolbar.configure(
            this,
            ToolbarConfiguration.Builder()
                .backButtonMode(BackButtonMode.Close(UIConfig.textTopBarSecondaryColor))
                .setSecondaryColors()
                .build()
        )
    }

    companion object {
        fun newInstance(cardId: String, tag: String) = AddCardOnboardingFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CARD_ID_KEY, cardId)
            }
            TAG = tag
        }
    }
}
