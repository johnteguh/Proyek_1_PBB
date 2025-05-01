package com.example.financetracker.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.example.financetracker.data.model.Category
import com.example.financetracker.data.model.Transaction
import com.example.financetracker.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

object TransactionRepository {
    private val incomeCategories = listOf(
        Category(name = "Gaji", color = Color(0xFF4CAF50), type = TransactionType.INCOME),
        Category(name = "Bonus", color = Color(0xFF8BC34A), type = TransactionType.INCOME),
        Category(name = "Investasi", color = Color(0xFF009688), type = TransactionType.INCOME),
        Category(name = "Lainnya", color = Color(0xFF2196F3), type = TransactionType.INCOME)
    )

    private val expenseCategories = listOf(
        Category(name = "Makanan", color = Color(0xFFF44336), type = TransactionType.EXPENSE),
        Category(name = "Transportasi", color = Color(0xFFFF9800), type = TransactionType.EXPENSE),
        Category(name = "Belanja", color = Color(0xFFE91E63), type = TransactionType.EXPENSE),
        Category(name = "Hiburan", color = Color(0xFF9C27B0), type = TransactionType.EXPENSE),
        Category(name = "Tagihan", color = Color(0xFF673AB7), type = TransactionType.EXPENSE),
        Category(name = "Lainnya", color = Color(0xFF607D8B), type = TransactionType.EXPENSE)
    )

    private val _categories = mutableStateListOf<Category>().apply {
        addAll(incomeCategories)
        addAll(expenseCategories)
    }

    val categories: List<Category> = _categories

    // Daftar transaksi
    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: SnapshotStateList<Transaction> = _transactions

    // Flow untuk perubahan transaksi
    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow.asStateFlow()

    // Menambahkan transaksi baru
    fun addTransaction(transaction: Transaction) {
        _transactions.add(transaction)
        updateFlow()
    }

    // Menghapus transaksi
    fun removeTransaction(transaction: Transaction) {
        _transactions.remove(transaction)
        updateFlow()
    }

    // Update transaksi
    fun updateTransaction(transaction: Transaction) {
        val index = _transactions.indexOfFirst { it.id == transaction.id }
        if (index >= 0) {
            _transactions[index] = transaction
            updateFlow()
        }
    }

    // Mendapatkan daftar kategori berdasarkan jenis transaksi
    fun getCategoriesByType(type: TransactionType): List<Category> {
        return categories.filter { it.type == type }
    }

    // Mendapatkan total saldo
    fun getBalance(): Double {
        return _transactions.sumOf {
            when (it.type) {
                TransactionType.INCOME -> it.amount
                TransactionType.EXPENSE -> -it.amount
            }
        }
    }

    // Mendapatkan total pemasukan
    fun getTotalIncome(): Double {
        return _transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }

    // Mendapatkan total pengeluaran
    fun getTotalExpense(): Double {
        return _transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    // Memperbarui flow
    private fun updateFlow() {
        _transactionsFlow.update { _transactions.toList() }
    }

    // Menambahkan data dummy untuk testing
    fun addSampleData() {
        // Beberapa transaksi contoh
        val transactions = listOf(
            Transaction(
                amount = 5000000.0,
                type = TransactionType.INCOME,
                category = categories.first { it.name == "Gaji" },
                description = "Gaji bulanan",
                date = Date()
            ),
            Transaction(
                amount = 500000.0,
                type = TransactionType.EXPENSE,
                category = categories.first { it.name == "Makanan" },
                description = "Belanja bulanan",
                date = Date()
            ),
            Transaction(
                amount = 300000.0,
                type = TransactionType.EXPENSE,
                category = categories.first { it.name == "Transportasi" },
                description = "Bensin",
                date = Date()
            ),
            Transaction(
                amount = 1000000.0,
                type = TransactionType.INCOME,
                category = categories.first { it.name == "Bonus" },
                description = "Bonus proyek",
                date = Date()
            )
        )

        _transactions.addAll(transactions)
        updateFlow()
    }


}