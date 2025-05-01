package com.example.financetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.financetracker.data.repository.TransactionRepository
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financetracker.ui.components.RecentTransactionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen() {
    val transactions by TransactionRepository.transactionsFlow.collectAsState()
    val sortedTransactions = transactions.sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Transaksi") }
            )
        }
    ) { paddingValues ->
        if (sortedTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                items(sortedTransactions) { transaction ->
//                    RecentTransactionItem(transaction = transaction)
//                    Divider()
//                }

                items(sortedTransactions.size) { index ->
                    val transaction = sortedTransactions[index]
                    RecentTransactionItem(transaction = transaction)
                    if (index < sortedTransactions.size - 1) {
                        Divider()
                    }
                }
            }
        }
    }
}
