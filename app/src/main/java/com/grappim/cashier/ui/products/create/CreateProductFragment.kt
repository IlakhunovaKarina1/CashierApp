package com.grappim.cashier.ui.products.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.databinding.FragmentCreateProductBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProductFragment : Fragment(R.layout.fragment_create_product) {

    private val viewModel: CreateProductViewModel by viewModels()
    private val viewBinding: FragmentCreateProductBinding by viewBinding(
        FragmentCreateProductBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        with(viewBinding) {
            buttonBack.setSafeOnClickListener {
                findNavController().popBackStack()
            }
            buttonCreateProduct.setSafeOnClickListener {

            }
        }
    }

    private fun observeViewModel() {
    }
}