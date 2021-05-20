package com.grappim.cashier.ui.create

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.asBigDecimal
import com.grappim.cashier.core.extensions.getErrorMessage
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.core.extensions.showToast
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.utils.ProductUnit
import com.grappim.cashier.core.view.CashierLoaderDialog
import com.grappim.cashier.databinding.FragmentCreateProductBinding
import com.grappim.cashier.ui.scanner.ScanType
import com.zhuinden.livedatacombinetuplekt.combineTupleNonNull
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateProductFragment : Fragment(R.layout.fragment_create_product) {

    private val viewModel: CreateProductViewModel by viewModels()
    private val viewBinding: FragmentCreateProductBinding by viewBinding(
        FragmentCreateProductBinding::bind
    )
    private val args by navArgs<CreateProductFragmentArgs>()
    private val loader: CashierLoaderDialog by lazy {
        CashierLoaderDialog(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun initViews() {
        with(viewBinding) {
            buttonCreateProduct.setSafeOnClickListener {
                viewModel.createProduct(
                    name = editName.text.toString(),
                    barcode = editBarcode.text.toString(),
                    sellingPrice = editSalesPrice.text.toString().asBigDecimal(),
                    purchasePrice = editCostPrice.text.toString().asBigDecimal(),
                    amount = viewModel.quantity.value!!.asBigDecimal(),
                    unit = viewModel.unit.value!!
                )
            }
            buttonBack.setSafeOnClickListener {
                findNavController().popBackStack()
            }
            buttonScan.setSafeOnClickListener {
                findNavController().navigate(
                    CreateProductFragmentDirections.actionCreateProductToScanner(
                        ScanType.SINGLE
                    )
                )
            }
            buttonMinus.setOnClickListener {
                viewModel.minusQuantity()
            }
            buttonPlus.setOnClickListener {
                viewModel.plusQuantity()
            }

            if (args.barcode?.isNotEmpty() == true) {
                viewBinding.editBarcode.setText(args.barcode)
            }
            chipGroupUnits.setOnCheckedChangeListener { group, checkedId ->
                val selectedChip = group.children
                    .toList()
                    .filter { (it as Chip).isChecked }
                    .joinToString {
                        (it as Chip).text.toString()
                    }
                viewModel.setUnit(ProductUnit.getProductUnitByValue(selectedChip))
            }

            ProductUnit.values().forEach {
                val chip = layoutInflater.inflate(
                    R.layout.layout_chip_unit,
                    null
                ) as Chip
                chip.text = it.value
                chip.tag = it
                chip.id = View.generateViewId()
                chipGroupUnits.addView(chip)
            }

            chipGroupUnits.check(chipGroupUnits[0].id)

            editSalesPrice.doAfterTextChanged {
                if (editSalesPrice.isFocused) {
                    viewModel.onSalesPriceChanged(it.toString())
                }
            }
            editCostPrice.doAfterTextChanged {
                if (editCostPrice.isFocused) {
                    viewModel.onCostPriceChanged(it.toString())
                }
            }
            editExtraPrice.doAfterTextChanged {
                if (editExtraPrice.isFocused) {
                    viewModel.onExtraPriceChanged(it.toString())
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.createProduct.observe(viewLifecycleOwner) {
            loader.showOrHide(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    showToast(getErrorMessage(it.exception))
                }
            }

        }

        viewModel.sellingPrice.observe(viewLifecycleOwner) {
            if (!viewBinding.editSalesPrice.isFocused) {
                viewBinding.editSalesPrice.setText(it)
            }
        }
        viewModel.purchasePrice.observe(viewLifecycleOwner) {
            if (!viewBinding.editCostPrice.isFocused) {
                viewBinding.editCostPrice.setText(it)
            }
        }
        viewModel.markup.observe(viewLifecycleOwner) {
            if (!viewBinding.editExtraPrice.isFocused) {
                viewBinding.editExtraPrice.setText(it)
            }
        }
        combineTupleNonNull(
            viewModel.quantity,
            viewModel.unit
        ).observe(viewLifecycleOwner) { (quantity: String, unit: ProductUnit) ->
            viewBinding.textQuantity.text = getString(
                R.string.title_quantity_and_unit,
                quantity,
                unit.value
            )
        }
    }
}