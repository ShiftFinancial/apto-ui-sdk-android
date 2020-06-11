package com.aptopayments.sdk.features.managecard

import com.aptopayments.core.data.transaction.Transaction
import com.aptopayments.core.exception.Failure
import com.aptopayments.core.functional.Either
import com.aptopayments.core.platform.AptoPlatform
import com.aptopayments.core.repository.transaction.TransactionListFilters

class FetchTransactionsTask(
    private val params: Params,
    private val onComplete: ((Either<Failure, List<Transaction>>) -> Unit)
) {

    data class Params(
        val cardId: String,
        val filters: TransactionListFilters,
        val forceApiCall: Boolean = true,
        val clearCachedValues: Boolean = false
    )

    var isExecuting = false
        private set
    private var isFinished = false
    private var isCancelled = false
        private set(cancelled) {
            field = cancelled
            if (cancelled) isExecuting = false
        }

    fun start() {
        if (!isCancelled) {
            isExecuting = true
            run()
        }
    }

    fun cancel() {
        isCancelled = true
    }

    private fun run() {
        if (!isCancelled) {
            AptoPlatform.fetchCardTransactions(
                cardId = params.cardId, filters = params.filters,
                forceRefresh = params.forceApiCall, clearCachedValues = params.clearCachedValues
            ) {
                onTransactionsFetched(it)
            }
        }
    }

    private fun onTransactionsFetched(it: Either<Failure, List<Transaction>>) {
        if (!isCancelled) {
            isExecuting = false
            isFinished = true
            onComplete(it)
        }
    }
}
