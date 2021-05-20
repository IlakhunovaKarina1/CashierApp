package com.grappim.cashier.core.utils

enum class ProductUnit(
    val value: String,
    val demonstrationTitle: String
) {
    PIECE("pc", "pc"),
    KG("kg", "kg"),
    GRAM("g", "g"),
    LITRE("l", "l"),
    METER("m", "m"),
    METER_2("m2", "m2"),
    METER_3("m3", "m3");

    companion object {
        fun getTitle(unit: String?): String =
            when (unit) {
                KG.value -> KG.demonstrationTitle
                PIECE.value -> PIECE.demonstrationTitle
                GRAM.value -> GRAM.demonstrationTitle
                LITRE.value -> LITRE.demonstrationTitle
                METER.value -> METER.demonstrationTitle
                METER_2.value -> METER_2.demonstrationTitle
                METER_3.value -> METER_3.demonstrationTitle
                else -> throw IllegalArgumentException("unit is null")
            }

        fun getValue(shortName: String?): String =
            when (shortName) {
                KG.demonstrationTitle -> KG.value
                PIECE.demonstrationTitle -> PIECE.value
                GRAM.demonstrationTitle -> GRAM.value
                LITRE.demonstrationTitle -> LITRE.value
                METER.demonstrationTitle -> METER.value
                METER_2.demonstrationTitle -> METER_2.value
                METER_3.demonstrationTitle -> METER_3.value
                else -> throw IllegalArgumentException("unit is null")
            }

        fun getProductUnitByValue(value: String?): ProductUnit =
            when (value) {
                KG.value -> KG
                PIECE.value -> PIECE
                GRAM.value -> GRAM
                LITRE.value -> LITRE
                METER.value -> METER
                METER_2.value -> METER_2
                METER_3.value -> METER_3
                else -> throw IllegalArgumentException("unit is null")
            }
    }
}