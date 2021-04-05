package com.grappim.cashier.ui.sales

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.bigDecimalOne
import com.grappim.cashier.core.extensions.bigDecimalZero
import com.grappim.cashier.core.extensions.getBlue
import com.grappim.cashier.core.extensions.getWhite
import com.grappim.cashier.core.extensions.inflate
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.core.extensions.setStandardSettings
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.databinding.ItemSalesProductBinding
import java.text.DecimalFormat

class SalesAdapter(
    private val dfSimple: DecimalFormat,
    private val clickListener: BasketProductClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val products: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SaleProductViewHolder(parent.inflate(R.layout.item_sales_product))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SaleProductViewHolder -> {
                with(holder) {
                    val product = products[holder.bindingAdapterPosition]
                    binding.textName.text = product.name
                    binding.textPrice.text = itemView.context.getString(
                        R.string.title_price_with_currency,
                        dfSimple.format(product.price)
                    )
                    binding.textBasketCount.text = itemView.context.getString(
                        R.string.title_basket_count,
                        dfSimple.format(product.basketCount)
                    )
                    binding.imageProduct.load(product.imageUrl) {
                        setStandardSettings()
                    }
                    if (product.basketCount <= bigDecimalZero()) {
                        binding.buttonBasket.backgroundTintList =
                            ColorStateList.valueOf(itemView.context.getWhite())
                        binding.buttonBasket.iconTint =
                            ColorStateList.valueOf(itemView.context.getBlue())
                    } else {
                        binding.buttonBasket.backgroundTintList =
                            ColorStateList.valueOf(itemView.context.getBlue())
                        binding.buttonBasket.iconTint =
                            ColorStateList.valueOf(itemView.context.getWhite())
                    }
                    binding.buttonBasket.setSafeOnClickListener {
                        onBasketClick(product, holder.bindingAdapterPosition)
                    }
                    binding.buttonPlus.setOnClickListener {
                        addProduct(product, holder.bindingAdapterPosition)
                    }
                    binding.buttonMinus.setOnClickListener {
                        minusProduct(product, holder.bindingAdapterPosition)
                    }
                }
            }
        }
    }

    private fun onBasketClick(product: Product, position: Int) {
        if (product.basketCount <= bigDecimalZero()) {
            addProduct(product, position)
        }
    }

    private fun addProduct(product: Product, position: Int) {
        product.basketCount = product.basketCount + bigDecimalOne()
        clickListener.addProduct(product)
        notifyItemChanged(position, product)
    }

    private fun minusProduct(product: Product, position: Int) {
        if (product.basketCount <= bigDecimalOne()) {
            product.basketCount = bigDecimalZero()
            clickListener.removeProduct(product)
            notifyItemChanged(position, product)
        } else {
            product.basketCount = product.basketCount - bigDecimalOne()
            clickListener.removeProduct(product)
            notifyItemChanged(position, product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    inner class SaleProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding by viewBinding(ItemSalesProductBinding::bind)
    }
}