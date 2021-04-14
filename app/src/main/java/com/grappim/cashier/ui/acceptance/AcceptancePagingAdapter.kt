package com.grappim.cashier.ui.acceptance

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.bigDecimalZero
import com.grappim.cashier.core.extensions.inflate
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.ItemAcceptanceBinding
import com.grappim.cashier.databinding.ItemAcceptanceHeaderBinding
import com.grappim.cashier.domain.acceptance.Acceptance
import java.text.DecimalFormat

class AcceptancePagingAdapter(
    private val listener: AcceptanceClickListener,
    private val dfSimple: DecimalFormat
) : PagingDataAdapter<PagingDataModel<Acceptance>, RecyclerView.ViewHolder>(DIFF_UTIL) {

    companion object {
        private const val TYPE_ACCEPTANCE = 0
        private const val TYPE_HEADER = 1

        val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingDataModel<Acceptance>>() {
            override fun areContentsTheSame(
                oldItem: PagingDataModel<Acceptance>,
                newItem: PagingDataModel<Acceptance>
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: PagingDataModel<Acceptance>,
                newItem: PagingDataModel<Acceptance>
            ): Boolean =
                (oldItem is PagingDataModel.Item && newItem is PagingDataModel.Item &&
                    oldItem.item.id == newItem.item.id)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position) ?: return) {
            is PagingDataModel.Separator -> {
                (holder as HeaderViewHolder).viewBinding.textDate.text = item.text
            }
            is PagingDataModel.Item -> {
                val acceptance = item.item

                with(holder as AcceptanceViewHolder) {
                    viewBinding.textVendor.text = acceptance.vendorName
                    viewBinding.textDateTime.text = acceptance.date
                    viewBinding.textPaidSum.text = itemView.context.getString(
                        R.string.title_price_with_currency,
                        dfSimple.format(acceptance.paidSum ?: bigDecimalZero())
                    )
                    viewBinding.textToPay.text = itemView.context.getString(
                        R.string.title_price_with_currency,
                        dfSimple.format(acceptance.toPay ?: bigDecimalZero())
                    )
                    when (acceptance.status) {
                        AcceptanceStatus.STANDARD -> {
                            viewBinding.textStatus.background = ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.shape_blue_round_15
                            )
                            viewBinding.textStatus.text = "Проведено"
                        }
                        AcceptanceStatus.PAID -> {
                            viewBinding.textStatus.background = ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.shape_green_round_15
                            )
                            viewBinding.textStatus.text = "Оплачено"
                        }
                    }

                    itemView.setSafeOnClickListener {
                        listener.onAcceptanceClick(acceptance)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(parent.inflate(R.layout.item_acceptance_header))
            else -> AcceptanceViewHolder(parent.inflate(R.layout.item_acceptance))
        }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is PagingDataModel.Separator -> TYPE_HEADER
            is PagingDataModel.Item -> TYPE_ACCEPTANCE
            null -> throw IllegalStateException("Unknown view")
        }

    inner class AcceptanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewBinding: ItemAcceptanceBinding by viewBinding(ItemAcceptanceBinding::bind)
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewBinding: ItemAcceptanceHeaderBinding by viewBinding(ItemAcceptanceHeaderBinding::bind)
    }
}