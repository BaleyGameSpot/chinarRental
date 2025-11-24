package com.chinarrental.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chinarrental.app.data.model.*
import com.chinarrental.app.ui.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    navController: NavController,
    viewModel: PaymentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payments") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Today's Total", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Rs. ${String.format(Locale.US, "%.0f", uiState.todayTotal)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("All Time Total", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Rs. ${String.format(Locale.US, "%.0f", uiState.totalAmount)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (uiState.payments.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No payments recorded yet")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.payments, key = { it.id }) { payment ->
                        PaymentCard(payment)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCard(payment: Payment) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = payment.paymentType.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(payment.paymentDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = payment.paymentMethod.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Rs. ${payment.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoznamchaScreen(
    navController: NavController,
    viewModel: RoznamchaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedType by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roznamcha (Ledger)") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog(true) }
            ) {
                Icon(Icons.Default.Add, "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Financial Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Income", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Rs. ${String.format(Locale.US, "%.0f", uiState.totalIncome)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Expense", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Rs. ${String.format(Locale.US, "%.0f", uiState.totalExpense)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Balance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Rs. ${String.format(Locale.US, "%.0f", uiState.balance)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedType == null,
                    onClick = { viewModel.filterByType(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.selectedType == TransactionType.INCOME,
                    onClick = { viewModel.filterByType(TransactionType.INCOME) },
                    label = { Text("Income") }
                )
                FilterChip(
                    selected = uiState.selectedType == TransactionType.EXPENSE,
                    onClick = { viewModel.filterByType(TransactionType.EXPENSE) },
                    label = { Text("Expense") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.transactions.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet. Tap + to add.")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.transactions, key = { it.id }) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                    }
                }
            }
        }
    }

    // Add Transaction Dialog
    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddDialog(false) },
            title = { Text("Add Transaction") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.newType.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedType) }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            TransactionType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        viewModel.updateNewType(type)
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.newCategory.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            TransactionCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.updateNewCategory(category)
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.newAmount,
                        onValueChange = { viewModel.updateNewAmount(it) },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.newDescription,
                        onValueChange = { viewModel.updateNewDescription(it) },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    uiState.error?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.addTransaction() }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TransactionCard(transaction: Transaction, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.type == TransactionType.INCOME)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}Rs. ${transaction.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == TransactionType.INCOME)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    navController: NavController,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Overdue", "Payment Due", "Completed")

    val filteredReminders = when (selectedFilter) {
        "Overdue" -> uiState.reminders.filter { it.type == ReminderType.OVERDUE && !it.isCompleted }
        "Payment Due" -> uiState.reminders.filter { it.type == ReminderType.PAYMENT_DUE && !it.isCompleted }
        "Completed" -> uiState.reminders.filter { it.isCompleted }
        else -> uiState.reminders
    }

    val overdueCount = uiState.reminders.count {
        (it.type == ReminderType.OVERDUE || it.reminderTime < System.currentTimeMillis()) && !it.isCompleted
    }
    val paymentDueCount = uiState.reminders.count { it.type == ReminderType.PAYMENT_DUE && !it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Reminders")
                        if (overdueCount > 0) {
                            Text(
                                "$overdueCount Overdue",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
        ) {
            // Summary Cards
            if (overdueCount > 0 || paymentDueCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (overdueCount > 0) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF44336)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "$overdueCount",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Overdue",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                    if (paymentDueCount > 0) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF9800)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "$paymentDueCount",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Payment Due",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                containerColor = Color.White,
                edgePadding = 16.dp
            ) {
                filters.forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = {
                            val count = when (filter) {
                                "Overdue" -> overdueCount
                                "Payment Due" -> paymentDueCount
                                "Completed" -> uiState.reminders.count { it.isCompleted }
                                else -> uiState.reminders.size
                            }
                            Text("$filter ($count)")
                        }
                    )
                }
            }

            if (filteredReminders.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No reminders in this category")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredReminders, key = { it.id }) { reminder ->
                        ReminderCard(reminder, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder, viewModel: RemindersViewModel) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val isOverdue = reminder.reminderTime < System.currentTimeMillis() && !reminder.isCompleted

    val backgroundColor = when {
        reminder.isCompleted -> Color(0xFFE8F5E9)
        reminder.type == ReminderType.OVERDUE -> Color(0xFFFFEBEE)
        reminder.type == ReminderType.PAYMENT_DUE -> Color(0xFFFFF3E0)
        isOverdue -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    val borderColor = when {
        reminder.type == ReminderType.OVERDUE -> Color(0xFFF44336)
        reminder.type == ReminderType.PAYMENT_DUE -> Color(0xFFFF9800)
        isOverdue -> Color(0xFFF44336)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (borderColor != Color.Transparent) {
            androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (reminder.type == ReminderType.OVERDUE) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (reminder.type == ReminderType.PAYMENT_DUE) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(Date(reminder.reminderTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) Color(0xFFF44336) else Color.Gray
                )
            }
            if (!reminder.isCompleted) {
                IconButton(onClick = { viewModel.markAsCompleted(reminder) }) {
                    Icon(Icons.Default.Check, "Mark Complete", tint = MaterialTheme.colorScheme.primary)
                }
            } else {
                Icon(Icons.Default.CheckCircle, "Completed", tint = Color(0xFF4CAF50))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreen(
    navController: NavController,
    viewModel: BillsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bills") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.bills.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No bills recorded")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.bills, key = { it.id }) { bill ->
                        BillCard(bill)
                    }
                }
            }
        }
    }
}

@Composable
fun BillCard(bill: Bill) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Bill #${bill.billNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(bill.billDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = bill.status.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Rs. ${bill.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    rentalsViewModel: RentalsViewModel = hiltViewModel()
) {
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val rentalsState by rentalsViewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Monthly") }
    val periods = listOf("Daily", "Weekly", "Monthly", "Yearly")

    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboardData()
        rentalsViewModel.loadRentals()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Analytics") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        periods.forEach { period ->
                            FilterChip(
                                selected = selectedPeriod == period,
                                onClick = { selectedPeriod = period },
                                label = { Text(period) },
                                leadingIcon = if (selectedPeriod == period) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            // Revenue Overview
            item {
                Text(
                    "Financial Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportCard(
                        title = "Total Revenue",
                        value = "PKR ${String.format("%.2f", dashboardState.totalIncome)}",
                        icon = Icons.Default.TrendingUp,
                        backgroundColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    ReportCard(
                        title = "Expenses",
                        value = "PKR ${String.format("%.2f", dashboardState.totalExpense)}",
                        icon = Icons.Default.TrendingDown,
                        backgroundColor = Color(0xFFF44336),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                ReportCard(
                    title = "Net Profit",
                    value = "PKR ${String.format("%.2f", dashboardState.balance)}",
                    icon = Icons.Default.AccountBalance,
                    backgroundColor = Primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Rental Statistics
            item {
                Text(
                    "Rental Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportCard(
                        title = "Active Rentals",
                        value = "${dashboardState.activeRentals}",
                        icon = Icons.Default.ShoppingCart,
                        backgroundColor = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    ReportCard(
                        title = "Overdue",
                        value = "${dashboardState.overdueRentals}",
                        icon = Icons.Default.Warning,
                        backgroundColor = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Customer & Item Stats
            item {
                Text(
                    "Business Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportCard(
                        title = "Total Customers",
                        value = "${dashboardState.totalCustomers}",
                        icon = Icons.Default.People,
                        backgroundColor = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                    ReportCard(
                        title = "Total Items",
                        value = "${dashboardState.totalItems}",
                        icon = Icons.Default.Inventory,
                        backgroundColor = Color(0xFF00BCD4),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Payment Statistics
            item {
                Text(
                    "Payment Analytics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Today's Collection",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            Text(
                                "PKR ${String.format("%.2f", dashboardState.todayIncome)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Pending Payments",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            Text(
                                "${dashboardState.pendingPayments}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }

            // Rental Status Distribution
            item {
                Text(
                    "Rental Status Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                val activeCount = rentalsState.rentals.count { it.status == RentalStatus.ACTIVE }
                val returnedCount = rentalsState.rentals.count { it.status == RentalStatus.RETURNED }
                val overdueCount = rentalsState.rentals.count { it.status == RentalStatus.OVERDUE }
                val cancelledCount = rentalsState.rentals.count { it.status == RentalStatus.CANCELLED }
                val total = rentalsState.rentals.size.toFloat().coerceAtLeast(1f)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusBar("Active", activeCount, total, Color(0xFF4CAF50))
                        StatusBar("Returned", returnedCount, total, Color(0xFF2196F3))
                        StatusBar("Overdue", overdueCount, total, Color(0xFFF44336))
                        StatusBar("Cancelled", cancelledCount, total, Color(0xFF9E9E9E))
                    }
                }
            }

            // Export Options
            item {
                Text(
                    "Export Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Export to PDF */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export PDF")
                    }
                    OutlinedButton(
                        onClick = { /* Export to Excel */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Excel")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusBar(
    label: String,
    count: Int,
    total: Float,
    color: Color
) {
    val percentage = (count / total * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                "$count ($percentage%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = count / total,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "App Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Backup & Sync", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Configure Google Drive backup, auto-sync settings")
                }
            }
        }
    }
}