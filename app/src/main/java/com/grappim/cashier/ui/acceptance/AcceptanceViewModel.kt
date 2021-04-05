package com.grappim.cashier.ui.acceptance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.domain.Acceptance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AcceptanceViewModel @Inject constructor(
    private val getAcceptanceListUseCase: GetAcceptanceListUseCase
) : ViewModel() {

    private val _acceptanceList = getAcceptanceListUseCase.invoke()
    val acceptanceList: LiveData<List<Acceptance>> =
        _acceptanceList.asLiveData(viewModelScope.coroutineContext)
}