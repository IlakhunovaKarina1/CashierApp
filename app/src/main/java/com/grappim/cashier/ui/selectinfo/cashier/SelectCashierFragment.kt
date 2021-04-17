package com.grappim.cashier.ui.selectinfo.cashier

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.databinding.FragmentSelectStockCashierBinding
import com.grappim.cashier.ui.selectinfo.stock.SelectInfoProgressAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCashierFragment : Fragment(R.layout.fragment_select_stock_cashier),
    CashierListClickListener {

    private val cashierAdapter: SelectCashierAdapter by lazy {
        SelectCashierAdapter(this)
    }
    private val binding: FragmentSelectStockCashierBinding by viewBinding(
        FragmentSelectStockCashierBinding::bind
    )
    private val selectInfoProgressAdapter: SelectInfoProgressAdapter by lazy {
        SelectInfoProgressAdapter()
    }

    private val viewModel: SelectCashierViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        onCashierClick()
    }

    private fun initViews() {
        with(binding) {
            textLabel.text = getString(R.string.title_checkout_selection)
            buttonNext.setSafeOnClickListener {
                viewModel.saveCashier(cashierAdapter.getSelectedItem()!!)
                findNavController().navigate(R.id.action_selectCashierFragment_to_menuFragment)
            }
            swipeRefresh.setOnRefreshListener {
                viewModel.getCashiers()
            }
            buttonBack.setSafeOnClickListener {
                findNavController().popBackStack()
            }
            recyclerItems.adapter = cashierAdapter
            recyclerItems.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recyclerProgress.adapter = selectInfoProgressAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.cashiers.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it is Resource.Loading
            when (it) {
                is Resource.Error -> {

                }
                is Resource.Success -> {
                    cashierAdapter.addItems(it.data)
                }
            }
        }
        selectInfoProgressAdapter.setItems(viewModel.stockProgresses)
    }

    override fun onCashierClick() {
        binding.buttonNext.isEnabled = cashierAdapter.getSelectedItem() != null
    }

}