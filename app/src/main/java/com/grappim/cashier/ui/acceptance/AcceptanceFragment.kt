package com.grappim.cashier.ui.acceptance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.domain.Acceptance
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.FragmentAcceptanceBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

@AndroidEntryPoint
class AcceptanceFragment : Fragment(R.layout.fragment_acceptance), AcceptanceClickListener {

    private val viewModel: AcceptanceViewModel by viewModels()
    private val viewBinding: FragmentAcceptanceBinding by viewBinding(FragmentAcceptanceBinding::bind)
    private val acceptanceAdapter: AcceptanceAdapter by lazy {
        AcceptanceAdapter(this)
    }

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
            recyclerAcceptance.adapter = ScaleInAnimationAdapter(acceptanceAdapter)
            recyclerAcceptance.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun observeViewModel() {
        viewModel.acceptanceList.observe(viewLifecycleOwner) {
            acceptanceAdapter.setItems(it)
        }
    }

    override fun onAcceptanceClick(acceptance: Acceptance) {
    }
}