package com.grappim.cashier.core.extensions

import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard2() {
    view?.let { activity?.hideKeyboard(it) }
}