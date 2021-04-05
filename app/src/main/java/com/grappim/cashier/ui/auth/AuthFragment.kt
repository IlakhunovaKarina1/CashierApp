package com.grappim.cashier.ui.auth

import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textfield.TextInputLayout
import com.grappim.cashier.R
import com.grappim.cashier.core.extensions.color
import com.grappim.cashier.core.extensions.hideKeyboard2
import com.grappim.cashier.core.extensions.setSafeOnClickListener
import com.grappim.cashier.core.extensions.underline
import com.grappim.cashier.databinding.FragmentAuthBinding
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.editPassword
import kotlinx.android.synthetic.main.fragment_auth.editPhoneNumber
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.widget.textChangeEvents
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding: FragmentAuthBinding by viewBinding(FragmentAuthBinding::bind)
    private var phoneNumberEntered: Boolean = false

    private val phoneMaskedTextChangedListener by lazy {
        MaskedTextChangedListener.installOn(
            binding.editPhoneNumber,
            "7 [000] [000] [00] [00]",
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                    Timber.d("masked: $maskFilled, $extractedValue | $formattedValue")
                    phoneNumberEntered = maskFilled
                    if (maskFilled) {
                        binding.tilPhoneNumber.endIconMode = TextInputLayout.END_ICON_CUSTOM
                        binding.tilPhoneNumber.endIconDrawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_check_circle_green
                        )
                    } else {
                        binding.tilPhoneNumber.endIconMode = TextInputLayout.END_ICON_NONE
                    }
                    checkDataToContinue()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            findNavController().navigate(R.id.action_authFragment_to_selectOutletFragment)
        }

        lifecycleScope.launch {
            binding.editPassword.textChangeEvents().collect {
                checkDataToContinue()
            }
        }
    }

    private fun checkDataToContinue() {
        val hasPasswordText = editPassword.text.toString().isNotBlank()
        binding.buttonSignIn.isEnabled = hasPasswordText && phoneNumberEntered
    }
}