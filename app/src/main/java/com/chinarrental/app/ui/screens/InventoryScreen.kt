package com.chinarrental.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.chinarrental.app.data.model.Item
import com.chinarrental.app.data.model.ItemCategory
import com.chinarrental.app.ui.navigation.Screen
import com.chinarrental.app.ui.theme.*
import com.chinarrental.app.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showCategoryFilter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory (${uiState.items.size})") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadItems() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, "Search")
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
                onClick = { navController.navigate(Screen.NewItem.route) }
            ) {
                Icon(Icons.Default.Add, "Add Item")
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
                visible = showSearchBar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchItems(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Search items...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = Primary)
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )
                }
            }

            // Category filter chips
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("All Categories") },
                            leadingIcon = if (uiState.selectedCategory == null) {
                                { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(ItemCategory.values().toList()) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.filterByCategory(category) },
                            label = { Text(category.name) },
                            leadingIcon = if (uiState.selectedCategory == category) {
                                { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor = Color.White
                            )
                        )
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
            } else if (uiState.items.isEmpty()) {
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
                        Text("No items in inventory. Tap + to add items.")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items, key = { it.id }) { item ->
                        ItemCard(
                            item = item,
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.loadItems() }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: Item, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val categoryColor = when (item.category.uppercase()) {
        "ELECTRONICS" -> CardGradient1
        "FURNITURE" -> CardGradient2
        "VEHICLES" -> CardGradient3
        "TOOLS" -> GradientOrange
        else -> Primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            categoryColor.copy(alpha = 0.08f),
                            categoryColor.copy(alpha = 0.02f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = categoryColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (item.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                    }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = ErrorLight
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    thickness = 1.dp,
                    color = TextHint.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Details Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ItemInfoCard(
                        icon = Icons.Default.CurrencyRupee,
                        label = "Rent per Day",
                        value = "Rs. ${String.format("%.0f", item.rentPerDay)}",
                        color = Primary
                    )

                    ItemInfoCard(
                        icon = Icons.Default.Inventory,
                        label = "Stock Available",
                        value = "${item.availableQuantity}/${item.totalQuantity}",
                        color = if (item.availableQuantity > 0) Success else Error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete ${item.name}?") },
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
fun ItemInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
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
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun LazyRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen(
    navController: NavController,
    viewModel: com.chinarrental.app.ui.viewmodel.NewItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedCategory by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item") },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Item Name *") },
                leadingIcon = { Icon(Icons.Default.Inventory2, null, tint = Primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                isError = uiState.error?.contains("name", ignoreCase = true) == true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description") },
                leadingIcon = { Icon(Icons.Default.Description, null, tint = Primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3,
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = if (uiState.isCustomCategory) "CUSTOM" else uiState.category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = Primary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    ItemCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            leadingIcon = { Icon(Icons.Default.Category, null) },
                            onClick = {
                                viewModel.updateCategory(category)
                                expandedCategory = false
                            }
                        )
                    }
                    Divider()
                    DropdownMenuItem(
                        text = { Text("CUSTOM") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = {
                            viewModel.setCustomCategoryMode(true)
                            expandedCategory = false
                        }
                    )
                }
            }

            AnimatedVisibility(visible = uiState.isCustomCategory) {
                OutlinedTextField(
                    value = uiState.customCategory,
                    onValueChange = { viewModel.updateCustomCategory(it) },
                    label = { Text("Custom Category Name *") },
                    leadingIcon = { Icon(Icons.Default.Edit, null, tint = Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    placeholder = { Text("e.g., STAGE_ITEMS") },
                    isError = uiState.error?.contains("category", ignoreCase = true) == true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )
            }

            OutlinedTextField(
                value = uiState.rentPerDay,
                onValueChange = { viewModel.updateRentPerDay(it) },
                label = { Text("Rent Per Day (Rs.) *") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null, tint = Primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                isError = uiState.error?.contains("rent", ignoreCase = true) == true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.totalQuantity,
                    onValueChange = { viewModel.updateTotalQuantity(it) },
                    label = { Text("Total Quantity *") },
                    leadingIcon = { Icon(Icons.Default.Numbers, null, tint = Primary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = uiState.error?.contains("quantity", ignoreCase = true) == true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )

                OutlinedTextField(
                    value = uiState.availableQuantity,
                    onValueChange = { viewModel.updateAvailableQuantity(it) },
                    label = { Text("Available *") },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Success) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = uiState.error?.contains("available", ignoreCase = true) == true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    )
                )
            }

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorLight
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = Error, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = Error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveItem() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                elevation = ButtonDefaults.buttonElevation(4.dp, 8.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Saving...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Save Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}