package com.grappim.cashier.ui.auth

import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textfield.TextInputLayout
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.color
import com.grappim.cashier.core.extensions.getErrorMessage
import com.grappim.cashier.core.extensions.hideKeyboard2
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.core.extensions.showToast
import com.grappim.cashier.core.extensions.underline
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.utils.PHONE_NUMBER_FORMAT
import com.grappim.cashier.core.view.CashierLoaderDialog
import com.grappim.cashier.core.workers.startTokenRefresher
import com.grappim.cashier.databinding.FragmentAuthBinding
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.zhuinden.livedatacombinetuplekt.combineTuple
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.editPassword
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChangeEvents
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding: FragmentAuthBinding by viewBinding(FragmentAuthBinding::bind)

    private val loader: CashierLoaderDialog by lazy {
        CashierLoaderDialog(requireContext())
    }
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var phoneMaskedTextChangedListener: MaskedTextChangedListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        combineTuple(viewModel.isPasswordNotBlank, viewModel.isFullPhoneNumberEntered)
            .observe(viewLifecycleOwner) { (isPasswordNotBlank, phoneFullyEntered) ->
                if (phoneFullyEntered == true) {
                    binding.tilPhoneNumber.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    binding.tilPhoneNumber.endIconDrawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_check_circle_green
                    )
                } else {
                    binding.tilPhoneNumber.endIconMode = TextInputLayout.END_ICON_NONE
                }

                binding.buttonSignIn.isEnabled = isPasswordNotBlank == true &&
                    phoneFullyEntered == true
            }

        viewModel.loginStatus.observe(viewLifecycleOwner) {
            loader.showOrHide(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_authFragment_to_selectOutletFragment)
                }
                is Resource.Error -> {
                    showToast(getErrorMessage(it.exception))
                }
            }
        }
    }

    private fun initViews() {
        phoneMaskedTextChangedListener = MaskedTextChangedListener.installOn(
            binding.editPhoneNumber,
            PHONE_NUMBER_FORMAT,
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                    Timber.d("masked: $maskFilled, $extractedValue | $formattedValue")

                    viewModel.onPhoneNumberEntered(maskFilled)
                    viewModel.setPhoneNumber(extractedValue)
                }
            }
        )
        binding.editPhoneNumber.hint = phoneMaskedTextChangedListener.placeholder()

        val textForForgotPass = SpannableString(
            getString(R.string.auth_forgot_password)
        )
            .color(ContextCompat.getColor(requireContext(), R.color.cashier_blue))
            .underline()
        binding.buttonForgotPassword.text = textForForgotPass
        binding.buttonForgotPassword.setSafeOnClickListener {

        }

        binding.buttonSignIn.setSafeOnClickListener {
            hideKeyboard2()
            viewModel.login(
                mobile = viewModel.phoneNumber.value!!,
                password = editPassword.text.toString()
            )
        }

        binding.editPassword.textChangeEvents().onEach {
            viewModel.onPasswordEntered(it.text.toString())
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

}