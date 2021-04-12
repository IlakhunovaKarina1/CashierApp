package com.grappim.cashier.ui.auth

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.cashier.core.functional.Resource
import com.grappim.cashier.core.functional.onFailure
import com.grappim.cashier.core.functional.onSuccess
import com.grappim.cashier.core.platform.SingleLiveEvent
import com.grappim.cashier.data.repository.GeneralRepository
import com.grappim.cashier.domain.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val generalRepository: GeneralRepository
) : ViewModel() {

    init {
        prePopulateDb()
    }

    private val _loginStatus = SingleLiveEvent<Resource<Unit>>()
    val loginStatus: LiveData<Resource<Unit>>
        get() = _loginStatus

    private fun prePopulateDb() {
        viewModelScope.launch {
            generalRepository.prePopulateDb()
        }
    }

    @MainThread
    fun login(
        mobile: String,
        password: String
    ) {
        viewModelScope.launch {
            _loginStatus.value = Resource.Loading
            loginUseCase.invoke(mobile, password)
                .onFailure {
                    _loginStatus.value = Resource.Error(it)
                }.onSuccess {
                    _loginStatus.value = Resource.Success(it)
                }
        }
    }

}