package com.aptopayments.sdk.features.loadfunds.paymentsources.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aptopayments.sdk.core.platform.theme.themeManager
import com.aptopayments.sdk.databinding.ViewPaymentSourceListItemBinding
import com.aptopayments.sdk.databinding.ViewPaymentSourceListNewBinding
import kotlinx.android.synthetic.main.view_section_option_subtitle_two.view.*

internal class PaymentSourcesListAdapter(val viewModel: PaymentSourcesListViewModel) :
    RecyclerView.Adapter<PaymentSourcesListAdapter.BaseViewHolder>() {

    private val items = mutableListOf<PaymentSourcesListItem>()

    override fun getItemViewType(position: Int): Int {
        return items[position].type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PaymentSourcesListItem.Type.EXISTING.value -> {
                ExistingPaymentSourceViewHolder(ViewPaymentSourceListItemBinding.inflate(layoutInflater, parent, false))
            }
            PaymentSourcesListItem.Type.NEW.value -> {
                NewPaymentSourceViewHolder(ViewPaymentSourceListNewBinding.inflate(layoutInflater, parent, false))
            }
            else -> throw RuntimeException()
        }
    }

    override fun getItemCount() = items.size

    fun setData(items: List<PaymentSourcesListItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    abstract inner class BaseViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        abstract fun bind(item: PaymentSourcesListItem)
    }

    inner class ExistingPaymentSourceViewHolder(private val binding: ViewPaymentSourceListItemBinding) :
        BaseViewHolder(binding.root) {

        override fun bind(item: PaymentSourcesListItem) {
            binding.element = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    inner class NewPaymentSourceViewHolder(private val binding: ViewPaymentSourceListNewBinding) :
        BaseViewHolder(binding.root) {

        init {
            with(themeManager()) {
                customizeSectionOptionIcon(binding.paymentSourceOptionNextIcon)
            }
        }

        override fun bind(item: PaymentSourcesListItem) {
            binding.element = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}
