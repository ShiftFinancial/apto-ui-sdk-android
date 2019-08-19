package com.aptopayments.sdk.core.platform

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aptopayments.core.exception.Failure
import com.aptopayments.core.extension.localized
import com.aptopayments.core.platform.AptoPlatformProtocol
import com.aptopayments.sdk.utils.MessageBanner
import org.koin.android.ext.android.inject
import java.lang.reflect.Modifier

private const val TAG_KEY = "APTO_TAG_KEY"

@VisibleForTesting(otherwise = Modifier.PROTECTED)
internal abstract class BaseDialogFragment : DialogFragment() {

    val aptoPlatformProtocol: AptoPlatformProtocol by inject()
    lateinit var TAG: String

    abstract fun layoutId(): Int

    abstract fun setUpUI()

    abstract fun setUpViewModel()

    abstract fun setUpListeners()

    protected open fun viewLoaded() {}

    protected lateinit var dialogView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(activity!!).inflate(layoutId(), null)
        if (savedInstanceState != null) {
            TAG = savedInstanceState.getString(TAG_KEY)!!
        }
        return AlertDialog.Builder(activity!!).setView(dialogView).create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return dialogView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TAG_KEY, TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        setUpUI()
        setUpListeners()
        viewLoaded()
    }

    protected fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError -> {
                context?.let { notify("failure_server_error".localized(it)) }
            }
            is Failure.UserSessionExpired -> {
                aptoPlatformProtocol.logout()
                context?.let { notify("session_expired_error".localized(it)) }
            }
        }
    }

    internal fun notify(message: String, messageType: MessageBanner.MessageType = MessageBanner.MessageType.ERROR) =
            activity?.let { MessageBanner(it).showBanner(message, messageType) }
}
