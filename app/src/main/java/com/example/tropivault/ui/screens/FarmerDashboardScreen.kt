package com.example.tropivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tropivault.data.local.ProductEntity
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.ProduceImage
import com.example.tropivault.ui.components.StatusBadge
import com.example.tropivault.ui.theme.*

/**
 * Dedicated, separate, fully functional Farmer Dashboard
 */
@Composable
fun FarmerDashboardScreen(
    viewModel: TropiVaultViewModel
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()

    val farmId = user?.id ?: 2
    val farmerProducts = remember(allProducts, farmId) {
        allProducts.filter { it.farmId == farmId || it.farmName == (user?.farmName ?: "Guimaras Heritage Orchards") }
    }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }

    // Interactive Vault Controls
    var targetVaultTemp by remember { mutableStateOf(12) }
    var nitrogenPurgeActive by remember { mutableStateOf(true) }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAdd = { name, category, description, price, unit, stock, temp, shelfLife, notes, imageUrl ->
                viewModel.addProduct(
                    name = name,
                    category = category,
                    description = description,
                    price = price,
                    unit = unit,
                    stockKg = stock,
                    storageTemp = temp,
                    shelfLifeDays = shelfLife,
                    preservationNotes = notes,
                    imageUrl = imageUrl,
                    autoApproved = false // Awaiting admin approval as requested!
                )
                showAddProductDialog = false
            }
        )
    }

    if (productToEdit != null) {
        EditProductDialog(
            product = productToEdit!!,
            onDismiss = { productToEdit = null },
            onSave = { id, name, price, stock, temp, days, notes ->
                viewModel.editProduct(id, name, price, stock, temp, days, notes)
                productToEdit = null
            }
        )
    }

    Scaffold(
        containerColor = TropiCream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = GoldenYellow,
                contentColor = DeepGreen,
                modifier = Modifier.testTag("farmer_add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Produce")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Farm Header
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GoldenYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Agriculture, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user?.farmName ?: "Guimaras Heritage Orchards",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "${user?.fullName ?: "Ramon Valderrama"} • ${user?.farmLocation ?: "Jordan, Guimaras"}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FarmerStatItem("Total Harvest", "₱48,920", "Gross Farmgate")
                            FarmerStatItem("Vault Stock", "${farmerProducts.sumOf { it.stockKg.toInt() }} kg", "Preserved")
                            FarmerStatItem("Target Temp", "${targetVaultTemp}°C", "Solar Chilled")
                        }
                    }
                }
            }

            // 2. Real-time Vault Preservation Telemetry & Controls
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sensors, contentDescription = null, tint = FreshGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Smart Vault Climate Controls",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepGreen
                                    )
                                )
                            }
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                                Text("ONLINE • ACTIVE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TelemetryPill(
                                "Oxygen Level",
                                if (nitrogenPurgeActive) "2.1% (N₂ Active)" else "5.8% (Standby)",
                                Icons.Default.Air
                            )
                            TelemetryPill("Relative Humidity", "91.8%", Icons.Default.WaterDrop)
                            TelemetryPill("Solar Battery", "98% (4.4 kW)", Icons.Default.SolarPower)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Controls Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Vault Temperature Setpoint", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepGreen)
                                Text("Adjust optimal chilling range", fontSize = 10.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledTonalIconButton(
                                    onClick = { if (targetVaultTemp > 8) targetVaultTemp-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease Temp", tint = ForestGreen)
                                }
                                Text(
                                    text = "${targetVaultTemp}°C",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DeepGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                FilledTonalIconButton(
                                    onClick = { if (targetVaultTemp < 18) targetVaultTemp++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase Temp", tint = ForestGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Nitrogen Controlled Atmosphere", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepGreen)
                                Text("Reduces respiration rate and spoilage", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = nitrogenPurgeActive,
                                onCheckedChange = { nitrogenPurgeActive = it }
                            )
                        }
                    }
                }
            }

            // 3. Product Listings Management Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Orchard Produce Listings (${farmerProducts.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    Button(
                        onClick = { showAddProductDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Produce", fontSize = 12.sp)
                    }
                }
            }

            // 4. Farmer's Products List
            if (farmerProducts.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "No produce listed yet. Click 'Add Produce' to register a new crop harvest with preservation specifications.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(farmerProducts, key = { it.id }) { product ->
                    FarmerProductCard(
                        product = product,
                        onStockChange = { delta ->
                            viewModel.updateProductStock(product.id, maxOf(0.0, product.stockKg + delta))
                        },
                        onEdit = { productToEdit = product },
                        onDelete = { viewModel.deleteProduct(product) }
                    )
                }
            }

            // 5. Incoming Orders to Fulfill
            item {
                Text(
                    text = "Customer Orders for Farm Dispatch",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
            }

            val recentOrders = orders.take(4)
            items(recentOrders) { ord ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ord.orderNumber, fontWeight = FontWeight.Bold, color = DeepGreen)
                            Text("₱%.2f".format(ord.totalAmount), fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                        Text("Customer: ${ord.customerName} • ${ord.fulfillmentType}", fontSize = 12.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Status: ${ord.orderStatus}", fontSize = 11.sp, color = TropicalOrange, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (ord.orderStatus == "PLACED") {
                                    OutlinedButton(
                                        onClick = { viewModel.updateOrderStatus(ord.id, "PROCESSING") },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Pack in Crate", fontSize = 11.sp, color = ForestGreen)
                                    }
                                } else if (ord.orderStatus == "PROCESSING") {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(ord.id, "READY_FOR_DISPATCH") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Ready for Pickup", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FarmerProductCard(
    product: ProductEntity,
    onStockChange: (Double) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProduceImage(
                product = product,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                Text("Category: ${product.category} • ₱%.0f / %s".format(product.price, product.unit), fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.isApproved) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                            Text("LIVE IN MARKET", modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), fontSize = 9.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(4.dp), color = GoldLight) {
                            Text("AWAITING ADMIN REVIEW", modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), fontSize = 9.sp, color = TropicalOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stock: %.0f kg".format(product.stockKg), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ForestGreen)
                }
            }

            // Edit, Delete & Quick stock adjustments
            Column(horizontalAlignment = Alignment.End) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ForestGreen, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onStockChange(-10.0) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "-10kg", tint = ForestGreen, modifier = Modifier.size(16.dp))
                    }
                    Text("+/-10", fontSize = 10.sp, color = Color.Gray)
                    IconButton(onClick = { onStockChange(10.0) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "+10kg", tint = ForestGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (Long, String, Double, Double, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var priceText by remember { mutableStateOf(product.price.toString()) }
    var stockText by remember { mutableStateOf(product.stockKg.toString()) }
    var temp by remember { mutableStateOf(product.storageTemp) }
    var shelfLifeText by remember { mutableStateOf(product.shelfLifeDaysRemaining.toString()) }
    var notes by remember { mutableStateOf(product.preservationNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Crop Details", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₱)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Storage Temp") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = shelfLifeText,
                        onValueChange = { shelfLifeText = it },
                        label = { Text("Shelf Life (Days)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Preservation Protocol") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull() ?: product.price
                    val s = stockText.toDoubleOrNull() ?: product.stockKg
                    val days = shelfLifeText.toIntOrNull() ?: product.shelfLifeDaysRemaining
                    onSave(product.id, name, p, s, temp, days, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Double, String, Double, String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Mangoes") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("250") }
    var unit by remember { mutableStateOf("per kg") }
    var stockText by remember { mutableStateOf("100") }
    var temp by remember { mutableStateOf("12°C") }
    var shelfLifeText by remember { mutableStateOf("16") }
    var notes by remember { mutableStateOf("Controlled Atmosphere Chilled at 12°C, 90% RH") }
    var imageUrl by remember { mutableStateOf("mango_guimaras_1790639653425") }
    var showAiScanner by remember { mutableStateOf(false) }

    val categories = listOf("Mangoes", "Bananas & Plantains", "Citrus & Melons", "Exotic & Rare", "Preserved & Dehydrated", "Farm Bundles")

    if (showAiScanner) {
        ProduceAiScannerDialog(
            onDismiss = { showAiScanner = false },
            onApplyScan = { scannedName, scannedCat, scannedPrice, scannedStock, scannedTemp, scannedLife, scannedNotes, scannedImg ->
                name = scannedName
                category = scannedCat
                priceText = scannedPrice.toInt().toString()
                stockText = scannedStock.toInt().toString()
                temp = scannedTemp
                shelfLifeText = scannedLife.toString()
                notes = scannedNotes
                description = "AI Inspected: Export Grade A+ quality harvest with high brix sweetness and zero lesions."
                imageUrl = scannedImg
                showAiScanner = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Produce in Vault", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AI Produce Scanner Button
                Button(
                    onClick = { showAiScanner = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = DeepGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auto-Detect Quality & Kilos (AI)", color = DeepGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                if (name.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LeafMint,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Image Attached: $imageUrl", fontSize = 11.sp, color = DeepGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product / Fruit Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                categories.forEach { cat ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = category == cat, onClick = { category = cat })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(cat, fontSize = 12.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₱)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (e.g. per kg)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Vault Temp") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = shelfLifeText,
                    onValueChange = { shelfLifeText = it },
                    label = { Text("Shelf-Life (Days)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Preservation Protocol") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Crop Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull() ?: 200.0
                    val s = stockText.toDoubleOrNull() ?: 50.0
                    val days = shelfLifeText.toIntOrNull() ?: 14
                    onAdd(name, category, description, p, unit, s, temp, days, notes, imageUrl)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Submit for Vault & Admin Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ProduceAiScannerDialog(
    onDismiss: () -> Unit,
    onApplyScan: (String, String, Double, Double, String, Int, String, String) -> Unit
) {
    var selectedProduceType by remember { mutableStateOf("MANGO") }
    var scanCompleted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = ForestGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Quality & Weight Scanner", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select harvest sample photo or capture produce image:", fontSize = 11.sp, color = Color.Gray)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { selectedProduceType = "MANGO"; scanCompleted = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedProduceType == "MANGO") ForestGreen else Color.LightGray),
                        modifier = Modifier.weight(1f).height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("🥭 Mango", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { selectedProduceType = "BANANA"; scanCompleted = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedProduceType == "BANANA") ForestGreen else Color.LightGray),
                        modifier = Modifier.weight(1f).height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("🍌 Banana", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { selectedProduceType = "DRAGONFRUIT"; scanCompleted = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedProduceType == "DRAGONFRUIT") ForestGreen else Color.LightGray),
                        modifier = Modifier.weight(1f).height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("🐉 Pitahaya", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { selectedProduceType = "POMELO"; scanCompleted = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedProduceType == "POMELO") ForestGreen else Color.LightGray),
                        modifier = Modifier.weight(1f).height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("🍊 Pomelo", fontSize = 10.sp)
                    }
                }

                // Visual Scanner Reticle Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (selectedProduceType) {
                                "MANGO" -> Icons.Default.Nature
                                "BANANA" -> Icons.Default.Spa
                                "DRAGONFRUIT" -> Icons.Default.Eco
                                else -> Icons.Default.WaterDrop
                            },
                            contentDescription = null,
                            tint = GoldenYellow,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (scanCompleted) "✨ Inspection Complete: 100% Goods" else "Target Harvest Loaded • Ready to inspect",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!scanCompleted) {
                    Button(
                        onClick = { scanCompleted = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run AI Inspection (Detect Quality & Kilos)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    // Inspection Results
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = LeafMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(shape = RoundedCornerShape(4.dp), color = FreshGreen) {
                                Text("✅ GOODS / EXPORT GRADE A+", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = when (selectedProduceType) {
                                    "MANGO" -> "Guimaras Sweet Carabao Mango • 45.0 kg (~180 pcs)"
                                    "BANANA" -> "Bukidnon Golden Lacatan Bananas • 65.0 kg bundle"
                                    "DRAGONFRUIT" -> "Mindanao Red Pitahaya • 38.0 kg harvest"
                                    else -> "Davao Pink Honey Pomelo • 52.0 kg crate"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepGreen
                            )
                            Text("Sweetness: 18.5° Brix • Skin Defects: 0.0%", fontSize = 10.sp, color = Color.DarkGray)
                            Text("Recommended Vault Temp: 12°C • Shelf-Life: 18 Days", fontSize = 10.sp, color = DeepGreen)
                        }
                    }

                    Button(
                        onClick = {
                            when (selectedProduceType) {
                                "MANGO" -> onApplyScan("Guimaras Super Sweet Carabao Mangoes", "Mangoes", 260.0, 45.0, "12°C", 18, "Nitrogen Cold Vault 12°C, 92% RH", "mango_guimaras_1790639653425")
                                "BANANA" -> onApplyScan("Bukidnon High-Altitude Lacatan Bananas", "Bananas & Plantains", 130.0, 65.0, "14°C", 14, "Ethylene-Scrubbed Chamber at 14°C", "banana_cavendish_1790639668532")
                                "DRAGONFRUIT" -> onApplyScan("Mindanao Red Pitahaya (Dragon Fruit)", "Exotic & Rare", 280.0, 38.0, "10°C", 16, "Solar Hydro-Chill Hypobaric Vault", "dragonfruit_pitahaya_1790639685341")
                                else -> onApplyScan("Davao Seedless Honey Pomelo", "Citrus & Melons", 190.0, 52.0, "11°C", 25, "Ozone-Sanitized Chilled Vault 11°C", "pomelo_citrus_1790639699116")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply AI Scan to Produce Form", color = DeepGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun FarmerStatItem(value: String, sub: String, label: String) {
    Column {
        Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        Text(sub, color = GoldenYellow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
fun TelemetryPill(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DeepGreen)
        Text(label, fontSize = 9.sp, color = Color.Gray)
    }
}
