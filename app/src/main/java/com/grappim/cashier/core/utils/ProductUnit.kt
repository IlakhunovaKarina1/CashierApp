package com.grappim.cashier.core.utils

enum class ProductUnit(val value: String, val title: String) {
    PIECE("pc", "шт"),
    KG("kg", "кг"),
    GRAM("g", "г"),
    LITRE("l", "л"),
    METER("m", "м"),
    METER_2("m2", "м.кв"),
    METER_3("m3", "м.куб");

    companion object {
        fun getTitle(unit: String?): String =
            when (unit) {
                KG.value -> KG.title
                PIECE.value -> PIECE.title
                GRAM.value -> GRAM.title
                LITRE.value -> LITRE.title
                METER.value -> METER.title
                METER_2.value -> METER_2.title
                METER_3.value -> METER_3.title
                else -> throw IllegalArgumentException("unit is null")
            }

        fun getValue(shortName: String?): String =
            when (shortName) {
                KG.title -> KG.value
                PIECE.title -> PIECE.value
                GRAM.title -> GRAM.value
                LITRE.title -> LITRE.value
                METER.title -> METER.value
                METER_2.title -> METER_2.value
                METER_3.title -> METER_3.value
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