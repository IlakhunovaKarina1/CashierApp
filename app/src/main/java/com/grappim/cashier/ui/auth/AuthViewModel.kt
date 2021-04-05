package com.grappim.cashier.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.data.repository.GeneralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val generalRepository: GeneralRepository
) : ViewModel() {

    fun prePopulateDb() {
        viewModelScope.launch {
            generalRepository.prePopulateDb()
        }
    }

}