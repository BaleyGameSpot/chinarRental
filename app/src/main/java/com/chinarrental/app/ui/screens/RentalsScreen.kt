package com.chinarrental.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chinarrental.app.data.model.*
import com.chinarrental.app.ui.navigation.Screen
import com.chinarrental.app.ui.theme.*
import com.chinarrental.app.ui.viewmodel.RentalsViewModel
import com.chinarrental.app.ui.viewmodel.NewRentalViewModel
import com.chinarrental.app.ui.viewmodel.RentalDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalsScreen(
    navController: NavController,
    viewModel: RentalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStatusFilter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rentals (${uiState.rentals.size})") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadRentals() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    IconButton(onClick = { showStatusFilter = !showStatusFilter }) {
                        Icon(Icons.Default.FilterList, "Filter")
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
                onClick = { navController.navigate(Screen.NewRental.route) }
            ) {
                Icon(Icons.Default.Add, "New Rental")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = showStatusFilter,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedStatus == null,
                                onClick = { viewModel.filterByStatus(null) },
                                label = { Text("All Rentals") },
                                leadingIcon = if (uiState.selectedStatus == null) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.selectedStatus == RentalStatus.ACTIVE,
                                onClick = { viewModel.filterByStatus(RentalStatus.ACTIVE) },
                                label = { Text("Active") },
                                leadingIcon = if (uiState.selectedStatus == RentalStatus.ACTIVE) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Success,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.selectedStatus == RentalStatus.RETURNED,
                                onClick = { viewModel.filterByStatus(RentalStatus.RETURNED) },
                                label = { Text("Returned") },
                                leadingIcon = if (uiState.selectedStatus == RentalStatus.RETURNED) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StatusReturned,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.rentals.isEmpty()) {
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
                        Text("No rentals found. Tap + to create a rental.")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.rentals, key = { it.id }) { rental ->
                        RentalCard(
                            rental = rental,
                            onReturn = { viewModel.returnRental(rental.id, System.currentTimeMillis()) },
                            onDelete = { viewModel.deleteRental(rental) },
                            onClick = {
                                navController.navigate("rental_detail/${rental.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RentalCard(
    rental: Rental,
    onReturn: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showReturnDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val statusColor = when (rental.status) {
        RentalStatus.ACTIVE -> Success
        RentalStatus.RETURNED -> StatusReturned
        RentalStatus.OVERDUE -> StatusOverdue
        RentalStatus.CANCELLED -> StatusCancelled
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.03f),
                            Accent.copy(alpha = 0.01f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rental #${rental.id}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = rental.status.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dates Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DateInfoItem(
                        icon = Icons.Default.CalendarToday,
                        label = "Start Date",
                        value = dateFormat.format(Date(rental.startDate)),
                        modifier = Modifier.weight(1f)
                    )
                    DateInfoItem(
                        icon = Icons.Default.EventAvailable,
                        label = "Return Date",
                        value = dateFormat.format(Date(rental.expectedReturnDate)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    thickness = 1.dp,
                    color = TextHint.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AmountInfoCard(
                        label = "Total Amount",
                        amount = "Rs. ${String.format("%.0f", rental.totalAmount)}",
                        icon = Icons.Default.CurrencyRupee,
                        color = Primary
                    )

                    AmountInfoCard(
                        label = "Remaining",
                        amount = "Rs. ${String.format("%.0f", rental.remainingAmount)}",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = if (rental.remainingAmount > 0) Error else Success
                    )
                }

                if (rental.status == RentalStatus.ACTIVE) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showReturnDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Success
                            )
                        ) {
                            Icon(
                                Icons.Default.AssignmentReturn,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Return", fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Error
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Error, Error))
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Return Rental") },
            text = { Text("Mark this rental as returned?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onReturn()
                        showReturnDialog = false
                    }
                ) {
                    Text("Return")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Rental") },
            text = { Text("Are you sure you want to delete this rental?") },
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

@Composable
fun DateInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun AmountInfoCard(
    label: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRentalScreen(
    navController: NavController,
    viewModel: NewRentalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedCustomer by remember { mutableStateOf(false) }
    var expandedItem by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showReturnDatePicker by remember { mutableStateOf(false) }
    var showReturnTimePicker by remember { mutableStateOf(false) }
    var selectedStartDateMillis by remember { mutableStateOf(uiState.startDate) }
    var selectedReturnDateMillis by remember { mutableStateOf(uiState.expectedReturnDate) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Rental") },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.08f),
                                    Accent.copy(alpha = 0.03f)
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Create New Rental",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Fill in the details below",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Selection
            ExposedDropdownMenuBox(
                expanded = expandedCustomer,
                onExpandedChange = { expandedCustomer = it }
            ) {
                OutlinedTextField(
                    value = uiState.selectedCustomer?.name ?: "Select Customer",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Customer *") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCustomer) },
                    isError = uiState.error?.contains("customer", ignoreCase = true) == true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedCustomer,
                    onDismissRequest = { expandedCustomer = false }
                ) {
                    uiState.customers.forEach { customer ->
                        DropdownMenuItem(
                            text = { Text("${customer.name} (${customer.phone})") },
                            onClick = {
                                viewModel.selectCustomer(customer)
                                expandedCustomer = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                    }
                }
            }

            // Item Selection
            ExposedDropdownMenuBox(
                expanded = expandedItem,
                onExpandedChange = { expandedItem = it }
            ) {
                OutlinedTextField(
                    value = uiState.selectedItem?.name ?: "Select Item",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Item *") },
                    leadingIcon = {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = Primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedItem) },
                    isError = uiState.error?.contains("item", ignoreCase = true) == true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedItem,
                    onDismissRequest = { expandedItem = false }
                ) {
                    uiState.items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text("${item.name} (Available: ${item.availableQuantity})") },
                            onClick = {
                                viewModel.selectItem(item)
                                expandedItem = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Inventory, contentDescription = null)
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.quantity,
                onValueChange = { viewModel.updateQuantity(it) },
                label = { Text("Quantity *") },
                leadingIcon = {
                    Icon(Icons.Default.ShoppingBasket, contentDescription = null, tint = Primary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                isError = uiState.error?.contains("quantity", ignoreCase = true) == true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            // Date and Time Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
                        .format(java.util.Date(selectedStartDateMillis)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start Date & Time *") },
                    leadingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date", tint = Primary)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Select Time", tint = Primary)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
                        .format(java.util.Date(selectedReturnDateMillis)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Expected Return Date & Time *") },
                    leadingIcon = {
                        IconButton(onClick = { showReturnDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date", tint = Primary)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { showReturnTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Select Time", tint = Primary)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )
            }

            // Guarantor Fields
            OutlinedTextField(
                value = uiState.guarantorName,
                onValueChange = { viewModel.updateGuarantorName(it) },
                label = { Text("Guarantor Name (Zimmedar)") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            OutlinedTextField(
                value = uiState.guarantorMobile,
                onValueChange = { viewModel.updateGuarantorMobile(it) },
                label = { Text("Guarantor Mobile Number") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Primary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            OutlinedTextField(
                value = uiState.advanceAmount,
                onValueChange = { viewModel.updateAdvanceAmount(it) },
                label = { Text("Advance Amount (Rs.)") },
                leadingIcon = {
                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = Primary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Notes") },
                leadingIcon = {
                    Icon(Icons.Default.Notes, contentDescription = null, tint = Primary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3,
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorLight
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = Error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.saveRental() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Creating...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Create Rental",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Start Date Picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedStartDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedStartDateMillis
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)

                        val newCalendar = Calendar.getInstance()
                        newCalendar.timeInMillis = millis
                        newCalendar.set(Calendar.HOUR_OF_DAY, hour)
                        newCalendar.set(Calendar.MINUTE, minute)

                        selectedStartDateMillis = newCalendar.timeInMillis
                        viewModel.updateStartDate(newCalendar.timeInMillis)
                    }
                    showStartDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Start Time Picker
    if (showStartTimePicker) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedStartDateMillis
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newCalendar = Calendar.getInstance()
                    newCalendar.timeInMillis = selectedStartDateMillis
                    newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newCalendar.set(Calendar.MINUTE, timePickerState.minute)

                    selectedStartDateMillis = newCalendar.timeInMillis
                    viewModel.updateStartDate(newCalendar.timeInMillis)
                    showStartTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    // Return Date Picker
    if (showReturnDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedReturnDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showReturnDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedReturnDateMillis
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)

                        val newCalendar = Calendar.getInstance()
                        newCalendar.timeInMillis = millis
                        newCalendar.set(Calendar.HOUR_OF_DAY, hour)
                        newCalendar.set(Calendar.MINUTE, minute)

                        selectedReturnDateMillis = newCalendar.timeInMillis
                        viewModel.updateExpectedReturnDate(newCalendar.timeInMillis)
                    }
                    showReturnDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Return Time Picker
    if (showReturnTimePicker) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedReturnDateMillis
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showReturnTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newCalendar = Calendar.getInstance()
                    newCalendar.timeInMillis = selectedReturnDateMillis
                    newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newCalendar.set(Calendar.MINUTE, timePickerState.minute)

                    selectedReturnDateMillis = newCalendar.timeInMillis
                    viewModel.updateExpectedReturnDate(newCalendar.timeInMillis)
                    showReturnTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalDetailsScreen(
    navController: NavController,
    rentalId: Long,
    viewModel: RentalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    var damageCharges by remember { mutableStateOf("") }
    var paymentAmount by remember { mutableStateOf("") }

    LaunchedEffect(rentalId) {
        viewModel.loadRentalDetails(rentalId)
    }

    LaunchedEffect(uiState.returnSuccess, uiState.paymentSuccess) {
        if (uiState.returnSuccess || uiState.paymentSuccess) {
            kotlinx.coroutines.delay(1500)
            viewModel.resetSuccess()
            if (uiState.returnSuccess) {
                navController.navigateUp()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rental Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Error loading rental details",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            val rental = uiState.rental
            val customer = uiState.customer
            val item = uiState.item

            if (rental != null && customer != null && item != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundLight)
                        .padding(paddingValues)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Badge
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (rental.status) {
                                    RentalStatus.ACTIVE -> if (rental.expectedReturnDate < System.currentTimeMillis()) Color(0xFFFF9800) else Color(0xFF4CAF50)
                                    RentalStatus.RETURNED -> Color(0xFF2196F3)
                                    RentalStatus.OVERDUE -> Color(0xFFF44336)
                                    RentalStatus.CANCELLED -> Color(0xFF9E9E9E)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Rental #${rental.id}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rental.status.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Customer Details Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Customer Details",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("Name", customer.name)
                                DetailRow("Mobile", customer.mobile)
                                DetailRow("CNIC", customer.cnic)
                                if (customer.address.isNotEmpty()) {
                                    DetailRow("Address", customer.address)
                                }
                            }
                        }
                    }

                    // Item Details Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Inventory,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Item Details",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("Item Name", item.name)
                                DetailRow("Category", item.category)
                                DetailRow("Quantity", rental.quantity.toString())
                            }
                        }
                    }

                    // Rental Duration Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Rental Duration",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("Start Date", dateFormat.format(Date(rental.startDate)))
                                DetailRow("Expected Return", dateFormat.format(Date(rental.expectedReturnDate)))
                                if (rental.actualReturnDate != null) {
                                    DetailRow("Actual Return", dateFormat.format(Date(rental.actualReturnDate)))
                                }
                                val days = ((rental.actualReturnDate ?: System.currentTimeMillis()) - rental.startDate) / (1000 * 60 * 60 * 24)
                                DetailRow("Total Days", days.toString())
                            }
                        }
                    }

                    // Amount Details Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Payment Details",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                DetailRow("Rent Per Day", "PKR ${String.format("%.2f", rental.rentPerDay)}")
                                DetailRow("Total Rent", "PKR ${String.format("%.2f", rental.totalRent)}")
                                if (rental.overdueRent > 0) {
                                    DetailRow("Overdue Charges", "PKR ${String.format("%.2f", rental.overdueRent)}", Color.Red)
                                }
                                if (rental.damageCharges > 0) {
                                    DetailRow("Damage Charges", "PKR ${String.format("%.2f", rental.damageCharges)}", Color.Red)
                                }
                                if (rental.discountAmount > 0) {
                                    DetailRow("Discount", "- PKR ${String.format("%.2f", rental.discountAmount)}", Color(0xFF4CAF50))
                                }
                                if (rental.advanceAmount > 0) {
                                    DetailRow("Advance Paid", "PKR ${String.format("%.2f", rental.advanceAmount)}")
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    thickness = 1.dp,
                                    color = Color.LightGray
                                )
                                DetailRow(
                                    "Final Amount",
                                    "PKR ${String.format("%.2f", rental.finalAmount)}",
                                    Primary,
                                    fontWeight = FontWeight.Bold
                                )
                                DetailRow(
                                    "Paid Amount",
                                    "PKR ${String.format("%.2f", rental.paidAmount)}",
                                    Color(0xFF4CAF50)
                                )
                                DetailRow(
                                    "Remaining Amount",
                                    "PKR ${String.format("%.2f", rental.remainingAmount)}",
                                    if (rental.remainingAmount > 0) Color.Red else Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Guarantor Details Card (if available)
                    if (rental.guarantorName.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.SupervisedUserCircle,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Guarantor Details",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    DetailRow("Name", rental.guarantorName)
                                    if (rental.guarantorMobile.isNotEmpty()) {
                                        DetailRow("Mobile", rental.guarantorMobile)
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons
                    if (rental.status == RentalStatus.ACTIVE) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.showPaymentDialog() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Payment")
                                }
                                Button(
                                    onClick = { viewModel.showReturnDialog() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.AssignmentReturn, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Return")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Return Dialog
    if (uiState.showReturnDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideReturnDialog() },
            title = { Text("Return Rental") },
            text = {
                Column {
                    Text("Are you sure you want to return this rental?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = damageCharges,
                        onValueChange = { damageCharges = it },
                        label = { Text("Damage Charges (Optional)") },
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Text("PKR", modifier = Modifier.padding(start = 12.dp))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val charges = damageCharges.toDoubleOrNull() ?: 0.0
                        viewModel.returnRental(charges)
                    }
                ) {
                    Text("Confirm Return")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideReturnDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Payment Dialog
    if (uiState.showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hidePaymentDialog() },
            title = { Text("Add Payment") },
            text = {
                Column {
                    Text("Enter payment amount:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = paymentAmount,
                        onValueChange = { paymentAmount = it },
                        label = { Text("Amount *") },
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Text("PKR", modifier = Modifier.padding(start = 12.dp))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Remaining: PKR ${String.format("%.2f", uiState.rental?.remainingAmount ?: 0.0)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = paymentAmount.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.addPayment(amount)
                            paymentAmount = ""
                        }
                    }
                ) {
                    Text("Add Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hidePaymentDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Snackbar
    if (uiState.returnSuccess) {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Rental returned successfully!")
        }
    }
    if (uiState.paymentSuccess) {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Payment added successfully!")
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = fontWeight,
            modifier = Modifier.weight(1f, fill = false),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
