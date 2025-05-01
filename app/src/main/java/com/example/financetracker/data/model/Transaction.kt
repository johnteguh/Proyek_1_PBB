package com.example.financetracker.data.model

import androidx.compose.ui.graphics.Color
import java.util.Date
import java.util.UUID

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Color,
    val type: TransactionType
)

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val description: String = "",
    val date: Date = Date()
)