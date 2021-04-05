package com.grappim.cashier.ui.acceptance

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.domain.Acceptance
import com.grappim.cashier.core.extensions.hide
import com.grappim.cashier.core.extensions.inflate
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.ItemAcceptanceBinding

class AcceptanceAdapter(
    private val listener: AcceptanceClickListener
) : RecyclerView.Adapter<AcceptanceAdapter.AcceptanceViewHolder>() {

    private val items = mutableListOf<Acceptance>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptanceViewHolder =
        AcceptanceViewHolder(parent.inflate(R.layout.item_acceptance))

    override fun onBindViewHolder(holder: AcceptanceViewHolder, position: Int) {
        with(holder) {
            val item = items[bindingAdapterPosition]
            viewBinding.textVendor.text = item.vendorName
            viewBinding.textDateTime.text = item.date
            viewBinding.textStatus.hide()
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