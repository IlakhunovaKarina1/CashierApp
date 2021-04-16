package com.grappim.cashier.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

const val categoryEntityTableName = "category_table"

@Entity(
    tableName = categoryEntityTableName
)
@Parcelize
data class CategoryEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    val isDefault: Boolean = false
) : Parcelable
