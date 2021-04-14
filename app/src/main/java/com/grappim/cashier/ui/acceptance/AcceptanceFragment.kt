package com.grappim.cashier.ui.acceptance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.FragmentAcceptanceBinding
import com.grappim.cashier.di.modules.DecimalFormatSimple
import com.grappim.cashier.domain.acceptance.Acceptance
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class AcceptanceFragment : Fragment(R.layout.fragment_acceptance), AcceptanceClickListener {

    @Inject
    @DecimalFormatSimple
    lateinit var dfSimple: DecimalFormat

    private val viewModel: AcceptanceViewModel by viewModels()
    private val viewBinding: FragmentAcceptanceBinding by viewBinding(FragmentAcceptanceBinding::bind)

    private val acceptancePagingAdapter: AcceptancePagingAdapter by lazy {
        AcceptancePagingAdapter(this, dfSimple)
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
            recyclerAcceptance.adapter = ScaleInAnimationAdapter(acceptancePagingAdapter)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenCreated {
            viewModel.acceptances.collectLatest {
                acceptancePagingAdapter.submitData(it)
            }
        }
    }

    override fun onAcceptanceClick(acceptance: Acceptance) {
    }
}