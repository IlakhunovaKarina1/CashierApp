package com.grappim.cashier.ui.acceptance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.grappim.cashier.core.extensions.getOffsetDateTimeWithFormatter
import com.grappim.cashier.core.utils.DateTimeUtils
import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.domain.acceptance.GetAcceptanceListPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AcceptanceViewModel @Inject constructor(
    getAcceptanceListPagingUseCase: GetAcceptanceListPagingUseCase
) : ViewModel() {

    private val df = DateTimeUtils.getDatePatternStandard()
    private val dtf = DateTimeUtils.getDateTimePatternStandard()

    val acceptances: Flow<PagingData<PagingDataModel<Acceptance>>> =
        getAcceptanceListPagingUseCase.invoke()
            .cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { PagingDataModel.Item(it) }
            }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }
                    val afterDate = df.format(
                        after.item.date.getOffsetDateTimeWithFormatter(formatter = dtf)
                    )
                    if (before == null) {
                        return@insertSeparators PagingDataModel.Separator(
                            afterDate
                        )
                    }
                    val beforeDate = df.format(
                        before.item.date.getOffsetDateTimeWithFormatter(formatter = dtf)
                    )

                    if (beforeDate != afterDate) {
                        PagingDataModel.Separator(afterDate)
                    } else {
                        null
                    }
                }
            }

}

sealed class PagingDataModel<out T> {
    data class Item<out T>(val item: T) : PagingDataModel<T>()
    data class Separator(val text: String) : PagingDataModel<Nothing>()
}