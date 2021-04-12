package com.grappim.cashier.ui.acceptance.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.domain.acceptance.GetAcceptanceListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AcceptanceViewModel @Inject constructor(
    getAcceptanceListUseCase: GetAcceptanceListUseCase
) : ViewModel() {

    private val _acceptanceList = getAcceptanceListUseCase.invoke()
    val acceptanceList: LiveData<List<Acceptance>> =
        _acceptanceList.asLiveData(viewModelScope.coroutineContext)
}