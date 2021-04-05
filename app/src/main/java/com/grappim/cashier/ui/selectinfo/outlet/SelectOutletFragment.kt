package com.grappim.cashier.ui.selectinfo.outlet

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
import com.grappim.cashier.databinding.FragmentSelectOutletCashierBinding
import com.grappim.cashier.ui.selectinfo.outlet.vm.SelectOutletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectOutletFragment : Fragment(R.layout.fragment_select_outlet_cashier),
    OutletListClickListener {

    private val outletAdapter: SelectOutletAdapter by lazy {
        SelectOutletAdapter(this)
    }
    private val binding: FragmentSelectOutletCashierBinding by viewBinding(
        FragmentSelectOutletCashierBinding::bind
    )
    private val selectInfoProgressAdapter: SelectInfoProgressAdapter by lazy {
        SelectInfoProgressAdapter()
    }

    private val viewModel: SelectOutletViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        onOutletClick()
    }

    private fun initViews() {
        with(binding) {
            buttonNext.setSafeOnClickListener {
                viewModel.saveOutlet(outletAdapter.getSelectedItem()!!)
                findNavController().navigate(R.id.action_selectOutletFragment_to_selectCashierFragment)
            }
            swipeRefresh.setOnRefreshListener {
                viewModel.getOutlets()
            }
            buttonBack.setSafeOnClickListener {
                requireActivity().onBackPressed()
            }

            recyclerItems.adapter = outletAdapter
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
        viewModel.outlets.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it is Resource.Loading
            when (it) {
                is Resource.Error -> {

                }
                is Resource.Success -> {
                    outletAdapter.addItems(it.data)
                }
            }
        }
        selectInfoProgressAdapter.setItems(viewModel.outletProgress)
    }

    override fun onOutletClick() {
        binding.buttonNext.isEnabled = outletAdapter.getSelectedItem() != null
    }
}