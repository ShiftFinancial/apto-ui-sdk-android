package com.aptopayments.sdk.features.card.transactionlist

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aptopayments.core.data.config.UIConfig
import com.aptopayments.core.data.transaction.Transaction
import com.aptopayments.sdk.R
import com.aptopayments.sdk.core.extension.observe
import com.aptopayments.sdk.core.extension.viewModel
import com.aptopayments.sdk.core.platform.BaseActivity
import com.aptopayments.sdk.core.platform.BaseFragment
import com.aptopayments.sdk.features.managecard.EndlessRecyclerViewScrollListener
import com.aptopayments.sdk.features.managecard.TransactionListItem
import kotlinx.android.synthetic.main.include_toolbar_two.*
import kotlinx.android.synthetic.main.transaction_list_fragment_theme_two.*
import java.lang.reflect.Modifier

private const val CARD_KEY = "CARD"
private const val CONFIG_KEY = "CONFIG"

@VisibleForTesting(otherwise = Modifier.PROTECTED)
internal class TransactionListFragmentThemeTwo : BaseFragment(), TransactionListContract.View,
        TransactionListAdapter.Delegate, SwipeRefreshLayout.OnRefreshListener {
    override var delegate: TransactionListContract.Delegate? = null
    override fun layoutId(): Int = R.layout.transaction_list_fragment_theme_two

    @VisibleForTesting(otherwise = Modifier.PRIVATE) lateinit var viewModel: TransactionListViewModel
    @VisibleForTesting(otherwise = Modifier.PRIVATE) lateinit var cardId: String
    @VisibleForTesting(otherwise = Modifier.PRIVATE) lateinit var config: TransactionListConfig
    private lateinit var transactionListAdapter: TransactionListAdapter
    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardId = arguments!![CARD_KEY] as String
        config = arguments!![CONFIG_KEY] as TransactionListConfig
    }

    override fun setupViewModel() {
        viewModel = viewModel(viewModelFactory) {
            observe(transactionListItems, ::handleTransactionList)
        }
    }

    private fun handleTransactionList(transactionList: List<TransactionListItem>?) {
        transactionList?.let { transactionListAdapter.transactionListItems = it }
    }

    override fun setupUI() {
        setupToolbar()
        setupRecyclerView()
        setupSwipeToRefresh()
    }

    private fun setupToolbar() {
        tb_llsdk_toolbar.setBackgroundColor(UIConfig.uiNavigationSecondaryColor)
        tb_llsdk_toolbar.setTitleTextColor(UIConfig.iconTertiaryColor)
        val title = context?.let { config.mcc.toString(it) }
        delegate?.configureToolbar(
                toolbar = tb_llsdk_toolbar,
                title = title,
                backButtonMode = BaseActivity.BackButtonMode.Back(title = null, color = UIConfig.textTopBarColor)
        )
    }

    private fun setupRecyclerView() {
        context?.let { context ->
            transactionListAdapter = TransactionListAdapter(ArrayList())
            val linearLayoutManager = LinearLayoutManager(context)
            scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    viewModel.fetchMoreTransaction(cardId, config.startDate, config.endDate, config.mcc) { count ->
                        if (count > 0) transactionListAdapter.notifyDataSetChanged()
                    }
                }
            }
            transactionListAdapter.delegate = this
            transactions_recycler_view.apply {
                layoutManager = linearLayoutManager
                addOnScrollListener(scrollListener!!)
                adapter = transactionListAdapter
            }
        }
    }

    override fun onTransactionTapped(transaction: Transaction) {
        delegate?.onTransactionTapped(transaction)
    }

    private fun setupSwipeToRefresh() {
        swipe_refresh_container.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        viewModel.fetchTransaction(cardId, config.startDate, config.endDate, config.mcc) {
            scrollListener?.resetState()
            swipe_refresh_container?.isRefreshing = false
        }
    }

    override fun viewLoaded() {
        viewModel.viewLoaded()
    }

    override fun onPresented() {
        super.onPresented()
        showLoading()
        viewModel.fetchTransaction(cardId, config.startDate, config.endDate, config.mcc) { hideLoading() }
    }

    override fun onBackPressed() {
        delegate?.onBackPressed()
    }

    companion object {
        fun newInstance(cardId: String, config: TransactionListConfig) = TransactionListFragmentThemeTwo().apply {
            arguments = Bundle().apply {
                putString(CARD_KEY, cardId)
                putSerializable(CONFIG_KEY, config)
            }
        }
    }
}
