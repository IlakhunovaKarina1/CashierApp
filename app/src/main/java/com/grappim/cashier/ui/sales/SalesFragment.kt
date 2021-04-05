package com.grappim.cashier.ui.sales

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.databinding.FragmentSalesBinding
import com.grappim.cashier.di.modules.DecimalFormatSimple
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.widget.textChanges
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class SalesFragment : Fragment(R.layout.fragment_sales), BasketProductClickListener {

    @Inject
    @DecimalFormatSimple
    lateinit var dfSimple: DecimalFormat

    private val viewBinding: FragmentSalesBinding by viewBinding(FragmentSalesBinding::bind)
    private val salesAdapter: SalesAdapter by lazy {
        SalesAdapter(dfSimple, this)
    }
    private val viewModel: SalesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        with(viewBinding) {
            buttonMenu.setSafeOnClickListener {
                requireActivity().onBackPressed()
            }
            recyclerProducts.adapter = ScaleInAnimationAdapter(salesAdapter)
            buttonBasket.setSafeOnClickListener {

            }

            lifecycleScope.launch {
                viewBinding.editSearchProducts
                    .textChanges()
                    .collect {
                        viewModel.searchProducts(it.toString())
                    }
            }

        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) {
            salesAdapter.updateProducts(it)
        }
        viewModel.basketCount.observe(viewLifecycleOwner) {
            viewBinding.buttonBasket.text = dfSimple.format(it)
        }
    }

    override fun addProduct(product: Product) {
        viewModel.addProductToBasket(product)
    }

    override fun removeProduct(product: Product) {
        viewModel.removeProductFromBasket(product)
    }
}