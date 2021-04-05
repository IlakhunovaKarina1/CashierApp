package com.grappim.cashier.ui.acceptance

import com.grappim.cashier.core.domain.Acceptance

interface AcceptanceClickListener {

    fun onAcceptanceClick(acceptance: Acceptance)

}