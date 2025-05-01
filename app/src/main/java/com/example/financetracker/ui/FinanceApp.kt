package com.example.financetracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.ui.screens.AddTransactionScreen
import com.example.financetracker.ui.screens.DashboardScreen
import com.example.financetracker.ui.screens.StatisticsScreen
import com.example.financetracker.ui.screens.TransactionsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector){
    object Dashboard : Screen("dashboard", "Beranda", Icons.Filled.Home)
    object Transactions : Screen("transactions", "Transaksi", Icons.Filled.List)
    object Statistics : Screen("statistics", "Statistik", Icons.Filled.Info)
    object AddTransaction : Screen("add_transaction", "Tambah Transaksi", Icons.Filled.Add)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Statistics
    )

    var showAddTransactionScreen by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = navController.currentDestination?.route == screen.route,
                        onClick = { navController.navigate(screen.route) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Transaksi")
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                    navigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    navigateToStatistics = { navController.navigate(Screen.Statistics.route) }
                )
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen()
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    onTransactionAdded = { navController.popBackStack() }
                )
            }
        }
    }
}
