package com.chinarrental.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chinarrental.app.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Warning : Screen("warning")
    object Dashboard : Screen("dashboard")
    object Rentals : Screen("rentals")
    object RentalDetail : Screen("rental_detail/{rentalId}")
    object NewRental : Screen("new_rental")
    object Customers : Screen("customers")
    object CustomerDetail : Screen("customer_detail/{customerId}")
    object NewCustomer : Screen("new_customer")
    object Inventory : Screen("inventory")
    object ItemDetail : Screen("item_detail/{itemId}")
    object NewItem : Screen("new_item")
    object Payments : Screen("payments")
    object Roznamcha : Screen("roznamcha")
    object Reminders : Screen("reminders")
    object Bills : Screen("bills")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Warning.route) {
            WarningScreen()
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Rentals.route) {
            RentalsScreen(navController)
        }
        composable(Screen.RentalDetail.route) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId")?.toLongOrNull() ?: 0L
            RentalDetailsScreen(navController, rentalId)
        }
        composable(Screen.NewRental.route) {
            NewRentalScreen(navController)
        }
        composable(Screen.Customers.route) {
            CustomersScreen(navController)
        }
        composable(Screen.CustomerDetail.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLongOrNull() ?: 0L
            CustomerDetailScreen(navController, customerId)
        }
        composable(Screen.NewCustomer.route) {
            NewCustomerScreen(navController)
        }
        composable(Screen.Inventory.route) {
            InventoryScreen(navController)
        }
        composable(Screen.NewItem.route) {
            NewItemScreen(navController)
        }
        composable(Screen.Payments.route) {
            PaymentsScreen(navController)
        }
        composable(Screen.Roznamcha.route) {
            RoznamchaScreen(navController)
        }
        composable(Screen.Reminders.route) {
            RemindersScreen(navController)
        }
        composable(Screen.Bills.route) {
            BillsScreen(navController)
        }
        composable(Screen.Reports.route) {
            ReportsScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
