package com.grappim.cashier.domain.login

import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.repository.GeneralRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val generalRepository: GeneralRepository
) {

    suspend operator fun invoke(
        mobile: String,
        password: String
    ): Either<Throwable, Unit> {
        val requestData = LoginRequestData(
            mobile = mobile,
            password = password
        )
        return generalRepository.login(requestData)
    }

    data class LoginRequestData(
        val mobile: String,
        val password: String
    )
}