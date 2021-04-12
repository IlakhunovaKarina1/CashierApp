package com.grappim.cashier.ui.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.databinding.FragmentProductsBinding
import com.grappim.cashier.di.modules.DecimalFormatSimple
import com.grappim.cashier.ui.products.vm.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_products.tabsCategories
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products),
    ProductsClickListener {

    @Inject
    @DecimalFormatSimple
    lateinit var dfSimple: DecimalFormat

    private val viewModel: ProductsViewModel by viewModels()
    private val binding: FragmentProductsBinding by viewBinding(FragmentProductsBinding::bind)
    private val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter(dfSimple, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        with(binding) {
            buttonMenu.setSafeOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonCreateProduct.setSafeOnClickListener {

            }
            recyclerProducts.adapter = ScaleInAnimationAdapter(productsAdapter)
            tabsCategories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    viewModel.setCategory(tab?.tag as Category)
                    viewModel.getProductsByCategory(tab?.tag as Category)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) {
            binding.tabsCategories.removeAllTabs()
            it.forEach {
                val tab = tabsCategories.newTab().apply {
                    text = it.name
                    tag = it
                }
                binding.tabsCategories.addTab(tab)
            }
        }
        viewModel.products.observe(viewLifecycleOwner) {
            productsAdapter.updateProducts(it)
        }
    }

    override fun onProductClick(product: Product) {
    }

}