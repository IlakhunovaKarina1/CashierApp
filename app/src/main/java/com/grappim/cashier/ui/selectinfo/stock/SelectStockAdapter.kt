package com.grappim.cashier.ui.selectinfo.stock

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grappim.cashier.R
import com.grappim.cashier.domain.outlet.Stock
import com.grappim.cashier.core.extensions.inflate
import com.grappim.cashier.core.extensions.showOrGone
import kotlinx.android.synthetic.main.item_select_info.view.imageChecked
import kotlinx.android.synthetic.main.item_select_info.view.textName

class SelectStockAdapter(
    private val listener: StockListClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Stock>()
    private var selectedItem: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SelectInfoViewHolder(parent.inflate(R.layout.item_select_info))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.run {
            val item = items[holder.bindingAdapterPosition]
            textName.text = item.name
            textName.isEnabled = selectedItem == holder.bindingAdapterPosition
            imageChecked.showOrGone(selectedItem == holder.bindingAdapterPosition)

            setOnClickListener {
                val oldPosition = selectedItem
                selectedItem = holder.bindingAdapterPosition
                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition)
                }
                if (oldPosition == holder.bindingAdapterPosition) {
                    selectedItem = -1
                }
                notifyItemChanged(holder.bindingAdapterPosition)
                listener.onOutletClick()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItems(newList: List<Stock>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun getSelectedItem(): Stock? = try {
        items[selectedItem]
    } catch (e: Exception) {
        null
    }

    inner class SelectInfoViewHolder(view: View) : RecyclerView.ViewHolder(view)
}