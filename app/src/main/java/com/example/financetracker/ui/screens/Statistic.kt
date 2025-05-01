package com.example.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financetracker.data.model.Category
import com.example.financetracker.data.model.Transaction
import com.example.financetracker.data.model.TransactionType
import com.example.financetracker.data.repository.TransactionRepository
import com.example.financetracker.ui.components.PieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen() {
    val transactions by TransactionRepository.transactionsFlow.collectAsState()

    val totalIncome = TransactionRepository.getTotalIncome()
    val totalExpense = TransactionRepository.getTotalExpense()

    // Mengelompokkan transaksi berdasarkan kategori
    val expensesByCategory = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
        .toList()
        .sortedByDescending { (_, amount) -> amount }

    val incomesByCategory = transactions
        .filter { it.type == TransactionType.INCOME }
        .groupBy { it.category }
        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
        .toList()
        .sortedByDescending { (_, amount) -> amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik") }
            )
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada data transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ringkasan
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ringkasan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pemasukan")
                            Text(
                                text = formatCurrency(totalIncome),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pengeluaran")
                            Text(
                                text = formatCurrency(totalExpense),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saldo")
                            Text(
                                text = formatCurrency(totalIncome - totalExpense),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Grafik Pengeluaran
                if (expensesByCategory.isNotEmpty()) {
                    ExpensePieChartSection(expensesByCategory, totalExpense)
                }

                // Grafik Pemasukan
                if (incomesByCategory.isNotEmpty()) {
                    IncomePieChartSection(incomesByCategory, totalIncome)
                }
            }
        }
    }
}

@Composable
fun ExpensePieChartSection(
    expensesByCategory: List<Pair<Category, Double>>,
    totalExpense: Double
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pengeluaran per Kategori",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    data = expensesByCategory.map { (category, amount) ->
                        PieChart.Slice(
                            value = amount,
                            color = category.color,
                            name = category.name
                        )
                    }
                )
            }

            // Daftar kategori dan persentase
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                expensesByCategory.forEach { (category, amount) ->
                    val percentage = (amount / totalExpense * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(category.color)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = category.name,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = formatCurrency(amount),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "$percentage%",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IncomePieChartSection(
    incomesByCategory: List<Pair<Category, Double>>,
    totalIncome: Double
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pemasukan per Kategori",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    data = incomesByCategory.map { (category, amount) ->
                        PieChart.Slice(
                            value = amount,
                            color = category.color,
                            name = category.name
                        )
                    }
                )
            }

            // Daftar kategori dan persentase
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                incomesByCategory.forEach { (category, amount) ->
                    val percentage = (amount / totalIncome * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(category.color)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = category.name,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = formatCurrency(amount),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "$percentage%",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
