package com.grappim.cashier.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu), MenuItemClickListener {

    private val viewModel by viewModels<MenuViewModel>()
    private val menuItemsAdapter: MenuAdapter by lazy {
        MenuAdapter(this)
    }
    private val viewBinding: FragmentMenuBinding by viewBinding(FragmentMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        with(viewBinding) {
            recyclerMenu.adapter = menuItemsAdapter
            buttonBack.setSafeOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.menuItems.observe(viewLifecycleOwner) {
            menuItemsAdapter.setItems(it)
        }
        viewModel.cashierName.observe(viewLifecycleOwner) {
            viewBinding.textCashier.text = it
        }
    }

    override fun onItemClick(item: MenuItem) {
        when (item.type) {
            MenuItemType.ACCEPTANCE -> {
                findNavController().navigate(R.id.action_menuFragment_to_acceptanceFragment)
            }
            MenuItemType.PRODUCTS -> {
                findNavController().navigate(R.id.action_menuFragment_to_productsFragment)
            }
            MenuItemType.SALES -> {
                findNavController().navigate(R.id.action_menuFragment_to_salesFragment)
            }
        }
    }

}