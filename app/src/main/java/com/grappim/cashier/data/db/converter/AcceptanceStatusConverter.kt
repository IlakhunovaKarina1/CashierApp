package com.grappim.cashier.data.db.converter

import androidx.room.TypeConverter
import com.grappim.cashier.ui.acceptance.AcceptanceStatus

class AcceptanceStatusConverter {

    @TypeConverter
    fun unitToString(status: AcceptanceStatus): String =
        status.value

    @TypeConverter
    fun stringToUnit(value: String): AcceptanceStatus =
        AcceptanceStatus.getStatusByValue(value)
}