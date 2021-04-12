package com.grappim.cashier.ui.acceptance

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.core.extensions.bigDecimalZero
import com.grappim.cashier.core.extensions.inflate
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.ItemAcceptanceBinding
import com.grappim.cashier.ui.acceptance.vm.AcceptanceStatus
import java.text.DecimalFormat

class AcceptanceAdapter(
    private val listener: AcceptanceClickListener,
    private val dfSimple: DecimalFormat
) : RecyclerView.Adapter<AcceptanceAdapter.AcceptanceViewHolder>() {

    private val items = mutableListOf<Acceptance>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptanceViewHolder =
        AcceptanceViewHolder(parent.inflate(R.layout.item_acceptance))

    override fun onBindViewHolder(holder: AcceptanceViewHolder, position: Int) {
        with(holder) {
            val item = items[bindingAdapterPosition]
            viewBinding.textVendor.text = item.vendorName
            viewBinding.textDateTime.text = item.date
            viewBinding.textPaidSum.text = itemView.context.getString(
                R.string.title_price_with_currency,
                dfSimple.format(item.paidSum ?: bigDecimalZero())
            )
            viewBinding.textToPay.text = itemView.context.getString(
                R.string.title_price_with_currency,
                dfSimple.format(item.toPay ?: bigDecimalZero())
            )
            when (item.status) {
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
                listener.onAcceptanceClick(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Acceptance>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class AcceptanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewBinding: ItemAcceptanceBinding by viewBinding(ItemAcceptanceBinding::bind)
    }
}